package com.FleetPulse.order.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.FleetPulse.customer.application.CustomerAddressService;
import com.FleetPulse.customer.application.CustomerService;
import com.FleetPulse.order.api.OrderRequests.CreateOrderRequest;
import com.FleetPulse.order.domain.CustomerOrder;
import com.FleetPulse.order.domain.OrderItem;
import com.FleetPulse.order.domain.Shipment;
import com.FleetPulse.order.domain.OrderStatusHistory;
import com.FleetPulse.order.infrastructure.CustomerOrderRepository;
import com.FleetPulse.order.infrastructure.OutboxEventRepository;
import com.FleetPulse.order.infrastructure.ShipmentRepository;
import com.FleetPulse.order.domain.OutboxEvent;
import com.FleetPulse.order.domain.ShipmentStatus;
import com.FleetPulse.platform.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@Transactional
public class OrderService {
	private final CustomerOrderRepository orders;
	private final CustomerService customers;
	private final CustomerAddressService addresses;
	private final OutboxEventRepository outbox;
	private final ObjectMapper objectMapper;
	private final ShipmentRepository shipments;

	public OrderService(CustomerOrderRepository orders, CustomerService customers, CustomerAddressService addresses,
			OutboxEventRepository outbox, ObjectMapper objectMapper, ShipmentRepository shipments) {
		this.orders = orders;
		this.customers = customers;
		this.addresses = addresses;
		this.outbox = outbox;
		this.objectMapper = objectMapper;
		this.shipments = shipments;
	}

	public CustomerOrder create(CreateOrderRequest request, String idempotencyKey, String correlationId) {
		UUID tenantId = TenantContext.requireTenantId();
		String requestHash = hash(request);
		var existing = orders.findByTenantIdAndIdempotencyKey(tenantId, idempotencyKey);
		if (existing.isPresent()) {
			if (!existing.get().matchesRequest(requestHash)) throw new IdempotencyConflictException();
			return existing.get();
		}

		customers.get(request.customerId());
		var destination = addresses.get(request.customerId(), request.destinationAddressId());
		BigDecimal subtotal = request.items().stream()
				.map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add).setScale(4, RoundingMode.HALF_UP);
		BigDecimal tax = request.tax().setScale(4, RoundingMode.HALF_UP);
		String orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(java.util.Locale.ROOT);
		CustomerOrder order = new CustomerOrder(tenantId, orderNumber, request.customerId(), request.currency(),
				subtotal, tax, idempotencyKey, requestHash);
		String actor = SecurityContextHolder.getContext().getAuthentication().getName();
		order.addStatusHistory(new OrderStatusHistory(tenantId, null, order.getStatus(), actor, "Order created"));
		request.items().forEach(item -> order.addItem(new OrderItem(tenantId, item.sku(), item.description(), item.quantity(),
				item.unitPrice().setScale(4, RoundingMode.HALF_UP))));
		UUID shipmentId;
		try {
			var snapshot = new DestinationAddressSnapshot(destination.getAddressType().name(), destination.getLine1(),
					destination.getLine2(), destination.getCity(), destination.getState(), destination.getPostalCode(),
					destination.getCountryCode(), destination.getLatitude(), destination.getLongitude());
			String snapshotJson = objectMapper.writeValueAsString(snapshot);
			String shipmentNumber = "SHP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(java.util.Locale.ROOT);
			Shipment shipment = new Shipment(tenantId, shipmentNumber, request.originWarehouseId(), snapshotJson);
			shipmentId = shipment.getId();
			shipment.addInitialStatus(actor);
			order.addShipment(shipment);
		} catch (JsonProcessingException exception) {
			throw new IllegalStateException("Could not serialize destination address", exception);
		}
		CustomerOrder saved = orders.save(order);
		try {
			OrderCreatedEnvelope event = new OrderCreatedEnvelope(UUID.randomUUID(), "OrderCreated", 1, java.time.Instant.now(),
					"order-service", tenantId, "CustomerOrder", saved.getId(), correlationId,
					new OrderCreatedPayload(saved.getId(), saved.getOrderNumber(), saved.getCustomerId(), saved.getCurrency(), saved.getTotal(),
						shipmentId, request.originWarehouseId(), request.items().stream().map(item -> new OrderLine(item.sku(), item.quantity())).toList()));
			outbox.save(new OutboxEvent("OrderCreated", "fleetpulse.order.order-created.v1", saved.getId(), objectMapper.writeValueAsString(event)));
		} catch (JsonProcessingException exception) {
			throw new IllegalStateException("Could not serialize order event", exception);
		}
		return saved;
	}

	@Transactional(readOnly = true)
	public CustomerOrder get(UUID id) {
		return orders.findByTenantIdAndId(TenantContext.requireTenantId(), id).orElseThrow(OrderNotFoundException::new);
	}

	public void cancelShipment(UUID shipmentId, String reason) {
		UUID tenantId = TenantContext.requireTenantId();
		var shipment = shipments.findByTenantIdAndId(tenantId, shipmentId).orElseThrow(ShipmentNotFoundException::new);
		String actor = SecurityContextHolder.getContext().getAuthentication().getName();
		shipment.getOrder().transitionTo(com.FleetPulse.order.domain.OrderStatus.CANCELLED, actor, reason);
		shipment.transitionTo(ShipmentStatus.CANCELLED, actor, reason);
		String correlationId = UUID.randomUUID().toString();
		try {
			OrderStatusChangedEnvelope event = new OrderStatusChangedEnvelope(UUID.randomUUID(), "ShipmentStatusChanged", 1,
					java.time.Instant.now(), "order-service", tenantId, "Shipment", shipmentId, correlationId,
					new OrderStatusChangedPayload(shipmentId, "CANCELLED", reason));
			outbox.save(new OutboxEvent("ShipmentStatusChanged", "fleetpulse.order.shipment-status-changed.v1", shipmentId,
					objectMapper.writeValueAsString(event)));
		} catch (JsonProcessingException exception) {
			throw new IllegalStateException("Could not serialize shipment status event", exception);
		}
		try {
			OrderStatusChangedEnvelope event = new OrderStatusChangedEnvelope(UUID.randomUUID(), "ShipmentStatusChanged", 1,
					java.time.Instant.now(), "order-service", tenantId, "Shipment", shipmentId, UUID.randomUUID().toString(),
					new OrderStatusChangedPayload(shipmentId, "CANCELLED", reason));
			outbox.save(new OutboxEvent("ShipmentStatusChanged", "fleetpulse.order.shipment-status-changed.v1", shipmentId,
					objectMapper.writeValueAsString(event)));
		} catch (JsonProcessingException exception) {
			throw new IllegalStateException("Could not serialize shipment status event", exception);
		}
	}

	private String hash(CreateOrderRequest request) {
		try {
			byte[] digest = MessageDigest.getInstance("SHA-256").digest(request.toString().getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(digest);
		} catch (NoSuchAlgorithmException exception) {
			throw new IllegalStateException("SHA-256 is unavailable", exception);
		}
	}

	public record OrderLine(String sku, long quantity) { }
	public record OrderCreatedPayload(UUID orderId, String orderNumber, UUID customerId, String currency, BigDecimal total,
			UUID shipmentId, UUID warehouseId, java.util.List<OrderLine> items) { }
	public record DestinationAddressSnapshot(String addressType, String line1, String line2, String city, String state,
			String postalCode, String countryCode, Double latitude, Double longitude) { }
	public record OrderCreatedEnvelope(UUID eventId, String eventType, int eventVersion, java.time.Instant occurredAt,
			String producer, UUID tenantId, String aggregateType, UUID aggregateId, String correlationId,
			OrderCreatedPayload payload) { }
	public record OrderStatusChangedPayload(UUID shipmentId, String status, String reason) { }
	public record OrderStatusChangedEnvelope(UUID eventId, String eventType, int eventVersion, java.time.Instant occurredAt,
			String producer, UUID tenantId, String aggregateType, UUID aggregateId, String correlationId,
			OrderStatusChangedPayload payload) { }
}
