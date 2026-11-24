package com.FleetPulse.inventory.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory_reservation")
public class InventoryReservation {
	@Id private UUID id;
	@Column(name = "tenant_id", nullable = false, updatable = false) private UUID tenantId;
	@Column(name = "shipment_id", nullable = false, updatable = false) private UUID shipmentId;
	@Column(name = "warehouse_id", nullable = false, updatable = false) private UUID warehouseId;
	@Column(nullable = false, length = 100, updatable = false) private String sku;
	@Column(nullable = false, updatable = false) private long quantity;
	@Enumerated(EnumType.STRING) @Column(nullable = false, length = 16) private ReservationStatus status;
	@Column(name = "idempotency_key", nullable = false, length = 100, updatable = false) private String idempotencyKey;
	@Column(name = "expires_at") private Instant expiresAt;
	@Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
	protected InventoryReservation() { }
	public InventoryReservation(UUID tenantId, UUID shipmentId, UUID warehouseId, String sku, long quantity, String key, Instant expiresAt) {
		this.id = UUID.randomUUID(); this.tenantId = tenantId; this.shipmentId = shipmentId; this.warehouseId = warehouseId;
		this.sku = sku; this.quantity = quantity; this.idempotencyKey = key; this.expiresAt = expiresAt; this.status = ReservationStatus.ACTIVE;
	}
	@PrePersist void onCreate() { createdAt = Instant.now(); }
	public void release() {
		if (status != ReservationStatus.ACTIVE) throw new IllegalStateException("Only active reservations can be released");
		status = ReservationStatus.RELEASED;
	}
	public UUID getId() { return id; }
	public UUID getShipmentId() { return shipmentId; }
	public UUID getWarehouseId() { return warehouseId; }
	public String getIdempotencyKey() { return idempotencyKey; }
	public String getSku() { return sku; }
	public long getQuantity() { return quantity; }
	public ReservationStatus getStatus() { return status; }
	public Instant getCreatedAt() { return createdAt; }
}
