package com.FleetPulse.inventory.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name = "stock_movement")
public class StockMovement {
	@Id private UUID id;
	@Column(name = "tenant_id", nullable = false, updatable = false) private UUID tenantId;
	@Column(name = "warehouse_id", nullable = false, updatable = false) private UUID warehouseId;
	@Column(nullable = false, length = 100, updatable = false) private String sku;
	@Enumerated(EnumType.STRING) @Column(name = "movement_type", nullable = false, length = 24, updatable = false) private StockMovementType movementType;
	@Column(nullable = false, updatable = false) private long quantity;
	@Column(name = "reference_type", length = 32) private String referenceType;
	@Column(name = "reference_id") private UUID referenceId;
	@Column(name = "occurred_at", nullable = false, updatable = false) private Instant occurredAt;
	protected StockMovement() { }
	public StockMovement(UUID tenantId, UUID warehouseId, String sku, StockMovementType type, long quantity, String referenceType, UUID referenceId) {
		this.id = UUID.randomUUID(); this.tenantId = tenantId; this.warehouseId = warehouseId; this.sku = sku;
		this.movementType = type; this.quantity = quantity; this.referenceType = referenceType; this.referenceId = referenceId;
		this.occurredAt = Instant.now();
	}
}
