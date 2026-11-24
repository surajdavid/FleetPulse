package com.FleetPulse.inventory.application;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;
import java.time.Instant;

import com.FleetPulse.inventory.api.InventoryRequests.CreateWarehouseRequest;
import com.FleetPulse.inventory.api.InventoryRequests;
import com.FleetPulse.inventory.api.InventoryRequests.ReserveInventoryRequest;
import com.FleetPulse.inventory.api.InventoryRequests.StockAdjustmentRequest;
import com.FleetPulse.inventory.domain.InsufficientInventoryException;
import com.FleetPulse.inventory.domain.InventoryItem;
import com.FleetPulse.inventory.domain.InventoryReservation;
import com.FleetPulse.inventory.domain.StockMovement;
import com.FleetPulse.inventory.domain.StockMovementType;
import com.FleetPulse.inventory.domain.Warehouse;
import com.FleetPulse.inventory.infrastructure.InventoryItemRepository;
import com.FleetPulse.inventory.infrastructure.InventoryReservationRepository;
import com.FleetPulse.inventory.infrastructure.StockMovementRepository;
import com.FleetPulse.inventory.infrastructure.WarehouseRepository;
import com.FleetPulse.order.domain.OutboxEvent;
import com.FleetPulse.order.infrastructure.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.FleetPulse.platform.tenant.TenantContext;
import com.FleetPulse.tenant.application.TenantAccessGuard;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InventoryService {
	private final WarehouseRepository warehouses;
	private final InventoryItemRepository items;
	private final InventoryReservationRepository reservations;
	private final StockMovementRepository movements;
	private final TenantAccessGuard tenantAccess;
	private final OutboxEventRepository outbox;
	private final ObjectMapper objectMapper;

	public InventoryService(WarehouseRepository warehouses, InventoryItemRepository items,
			InventoryReservationRepository reservations, StockMovementRepository movements, TenantAccessGuard tenantAccess,
			OutboxEventRepository outbox, ObjectMapper objectMapper) {
		this.warehouses = warehouses; this.items = items; this.reservations = reservations; this.movements = movements;
		this.tenantAccess = tenantAccess;
		this.outbox = outbox; this.objectMapper = objectMapper;
	}

	public Warehouse createWarehouse(CreateWarehouseRequest request) {
		UUID tenantId = TenantContext.requireTenantId();
		tenantAccess.requireActive(tenantId);
		if (warehouses.existsByTenantIdAndCode(tenantId, request.code())) throw new WarehouseCodeConflictException();
		return warehouses.save(new Warehouse(tenantId, request.code(), request.name(), request.address()));
	}

	@Transactional(readOnly = true)
	public List<InventoryItem> getInventory(UUID warehouseId) {
		UUID tenantId = TenantContext.requireTenantId();
		tenantAccess.requireActive(tenantId);
		warehouses.findByTenantIdAndId(tenantId, warehouseId).orElseThrow(WarehouseNotFoundException::new);
		return items.findAllByTenantIdAndWarehouseId(tenantId, warehouseId);
	}

	public InventoryItem adjustStock(UUID warehouseId, StockAdjustmentRequest request) {
		UUID tenantId = TenantContext.requireTenantId();
		tenantAccess.requireActive(tenantId);
		warehouses.findByTenantIdAndId(tenantId, warehouseId).orElseThrow(WarehouseNotFoundException::new);
		InventoryItem item = items.findByTenantIdAndWarehouseIdAndSku(tenantId, warehouseId, request.sku()).orElse(null);
		if (request.type() == InventoryRequests.StockAdjustmentType.RECEIVE) {
			if (item == null) item = new InventoryItem(tenantId, warehouseId, request.sku());
			item.receive(request.quantity());
			item = items.save(item);
			movements.save(new StockMovement(tenantId, warehouseId, request.sku(), StockMovementType.RECEIPT,
					request.quantity(), "MANUAL", null));
			return item;
		}
		int changed = items.issueIfAvailable(tenantId, warehouseId, request.sku(), request.quantity());
		if (changed == 0) throw new InsufficientInventoryException(request.sku());
		movements.save(new StockMovement(tenantId, warehouseId, request.sku(), StockMovementType.ISSUE,
				request.quantity(), "MANUAL", null));
		return items.findByTenantIdAndWarehouseIdAndSku(tenantId, warehouseId, request.sku()).orElseThrow();
	}

	public List<InventoryReservation> reserve(ReserveInventoryRequest request, String idempotencyKey) {
		UUID tenantId = TenantContext.requireTenantId();
		tenantAccess.requireActive(tenantId);
		return reserveForTenant(tenantId, request, idempotencyKey);
	}

	public List<InventoryReservation> reserveForOrderEvent(UUID tenantId, ReserveInventoryRequest request,
			String idempotencyKey, String correlationId) {
		tenantAccess.requireActive(tenantId);
		List<InventoryReservation> reserved = reserveForTenant(tenantId, request, idempotencyKey);
		try {
			InventoryEvent event = new InventoryEvent(UUID.randomUUID(), "InventoryReserved", 1, Instant.now(), "inventory-service",
					tenantId, "InventoryReservation", reserved.getFirst().getId(), correlationId,
					new InventoryEventPayload(request.shipmentId(), request.warehouseId(), reserved.stream()
							.map(item -> new ReservedItem(item.getSku(), item.getQuantity())).toList()));
			outbox.save(new OutboxEvent("InventoryReserved", "fleetpulse.inventory.inventory-reserved.v1",
					reserved.getFirst().getId(), objectMapper.writeValueAsString(event)));
		} catch (JsonProcessingException exception) { throw new IllegalStateException("Could not serialize inventory event", exception); }
		return reserved;
	}

	public void publishRejected(UUID tenantId, UUID shipmentId, String correlationId, String sku) {
		try {
			InventoryEvent event = new InventoryEvent(UUID.randomUUID(), "InventoryRejected", 1, Instant.now(), "inventory-service",
					tenantId, "Shipment", shipmentId, correlationId, new InventoryRejectedPayload(shipmentId, sku));
			outbox.save(new OutboxEvent("InventoryRejected", "fleetpulse.inventory.inventory-rejected.v1", shipmentId,
					objectMapper.writeValueAsString(event)));
		} catch (JsonProcessingException exception) { throw new IllegalStateException("Could not serialize inventory event", exception); }
	}

	private List<InventoryReservation> reserveForTenant(UUID tenantId, ReserveInventoryRequest request, String idempotencyKey) {
		Set<String> uniqueSkus = new HashSet<>();
		if (request.items().stream().anyMatch(line -> !uniqueSkus.add(line.sku()))) throw new IllegalArgumentException("Reservation contains duplicate SKUs");
		warehouses.findByTenantIdAndId(tenantId, request.warehouseId()).orElseThrow(WarehouseNotFoundException::new);
		List<InventoryReservation> existing = reservations.findAllByTenantIdAndIdempotencyKey(tenantId, idempotencyKey);
		if (!existing.isEmpty()) {
			boolean matches = existing.size() == request.items().size() && existing.stream().allMatch(saved ->
					saved.getShipmentId().equals(request.shipmentId()) && saved.getWarehouseId().equals(request.warehouseId()) &&
						request.items().stream().anyMatch(line -> line.sku().equals(saved.getSku()) && line.quantity() == saved.getQuantity()));
			if (!matches) throw new ReservationConflictException();
			return existing;
		}
		List<InventoryReservation> created = new ArrayList<>();
		for (var line : request.items()) {
			int reserved = items.reserveIfAvailable(tenantId, request.warehouseId(), line.sku(), line.quantity());
			if (reserved == 0) throw new InsufficientInventoryException(line.sku());
			InventoryReservation reservation = new InventoryReservation(tenantId, request.shipmentId(), request.warehouseId(),
					line.sku(), line.quantity(), idempotencyKey, Instant.now().plusSeconds(900));
			created.add(reservations.save(reservation));
			movements.save(new StockMovement(tenantId, request.warehouseId(), line.sku(), StockMovementType.RESERVATION,
					line.quantity(), "SHIPMENT", request.shipmentId()));
		}
		return created;
	}

	public void releaseReservation(UUID reservationId) {
		UUID tenantId = TenantContext.requireTenantId();
		tenantAccess.requireActive(tenantId);
		InventoryReservation reservation = reservations.findByTenantIdAndId(tenantId, reservationId)
				.orElseThrow(ReservationNotFoundException::new);
		reservation.release();
		int updated = items.releaseReservation(tenantId, reservation.getWarehouseId(), reservation.getSku(), reservation.getQuantity());
		if (updated == 0) throw new IllegalStateException("Reserved quantity is inconsistent");
		movements.save(new StockMovement(tenantId, reservation.getWarehouseId(), reservation.getSku(),
				StockMovementType.RESERVATION_RELEASE, reservation.getQuantity(), "RESERVATION", reservationId));
	}
	public record ReservedItem(String sku, long quantity) { }
	public record InventoryEventPayload(UUID shipmentId, UUID warehouseId, List<ReservedItem> items) { }
	public record InventoryRejectedPayload(UUID shipmentId, String sku) { }
	public record InventoryEvent(UUID eventId, String eventType, int eventVersion, Instant occurredAt, String producer,
			UUID tenantId, String aggregateType, UUID aggregateId, String correlationId, Object payload) { }
}
