package com.FleetPulse.order.application;

import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.FleetPulse.order.domain.CustomerOrder;
import com.FleetPulse.order.domain.OrderStatus;
import com.FleetPulse.order.infrastructure.CustomerOrderRepository;
import com.FleetPulse.order.infrastructure.ShipmentRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PaymentOutcomeConsumer {
	private final CustomerOrderRepository orders;
	private final ObjectMapper objectMapper;
	private final ShipmentRepository shipments;

	public PaymentOutcomeConsumer(CustomerOrderRepository orders, ShipmentRepository shipments, ObjectMapper objectMapper) {
		this.orders = orders;
		this.shipments = shipments;
		this.objectMapper = objectMapper;
	}

	@KafkaListener(topics = "fleetpulse.inventory.inventory-reserved.v1", groupId = "order-service")
	@Transactional
	public void onInventoryReserved(String message) throws Exception { applyInventoryResult(message, true); }

	@KafkaListener(topics = "fleetpulse.inventory.inventory-rejected.v1", groupId = "order-service")
	@Transactional
	public void onInventoryRejected(String message) throws Exception { applyInventoryResult(message, false); }

	@KafkaListener(topics = "fleetpulse.payment.payment-completed.v1", groupId = "order-service")
	@Transactional
	public void onPaymentCompleted(String message) throws Exception { apply(message, OrderStatus.CONFIRMED, "Payment completed"); }

	@KafkaListener(topics = "fleetpulse.payment.payment-failed.v1", groupId = "order-service")
	@Transactional
	public void onPaymentFailed(String message) throws Exception { apply(message, OrderStatus.PAYMENT_FAILED, "Payment failed"); }

	private void apply(String message, OrderStatus targetStatus, String reason) throws Exception {
		JsonNode envelope = objectMapper.readTree(message);
		UUID tenantId = UUID.fromString(envelope.path("tenantId").asText());
		UUID orderId = UUID.fromString(envelope.path("payload").path("orderId").asText());
		CustomerOrder order = orders.findByTenantIdAndId(tenantId, orderId).orElseThrow(OrderNotFoundException::new);
		if (order.getStatus() == OrderStatus.PAYMENT_PENDING) {
			order.transitionTo(targetStatus, "payment-service", reason);
		}
	}

	private void applyInventoryResult(String message, boolean accepted) throws Exception {
		JsonNode envelope = objectMapper.readTree(message);
		UUID tenantId = UUID.fromString(envelope.path("tenantId").asText());
		UUID shipmentId = UUID.fromString(envelope.path("payload").path("shipmentId").asText());
		var shipment = shipments.findByTenantIdAndId(tenantId, shipmentId).orElseThrow(ShipmentNotFoundException::new);
		CustomerOrder order = shipment.getOrder();
		if (order.getStatus() == OrderStatus.CREATED) {
			if (accepted) order.transitionTo(OrderStatus.PAYMENT_PENDING, "inventory-service", "Inventory reserved");
			else {
				order.transitionTo(OrderStatus.CANCELLED, "inventory-service", "Inventory reservation rejected");
				shipment.transitionTo(com.FleetPulse.order.domain.ShipmentStatus.CANCELLED, "inventory-service", "Inventory reservation rejected");
			}
		}
	}
}
