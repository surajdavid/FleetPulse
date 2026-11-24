package com.FleetPulse.inventory.domain;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

@Entity
@Table(name = "inventory_item", uniqueConstraints = @UniqueConstraint(name = "uk_inventory_tenant_warehouse_sku", columnNames = {"tenant_id", "warehouse_id", "sku"}))
public class InventoryItem {
	@Id private UUID id;
	@Column(name = "tenant_id", nullable = false, updatable = false) private UUID tenantId;
	@Column(name = "warehouse_id", nullable = false, updatable = false) private UUID warehouseId;
	@Column(nullable = false, length = 100, updatable = false) private String sku;
	@Column(name = "quantity_on_hand", nullable = false) private long quantityOnHand;
	@Column(name = "quantity_reserved", nullable = false) private long quantityReserved;
	@Version private long version;
	protected InventoryItem() { }
	public InventoryItem(UUID tenantId, UUID warehouseId, String sku) {
		this.id = UUID.randomUUID(); this.tenantId = tenantId; this.warehouseId = warehouseId; this.sku = sku;
	}
	public void receive(long quantity) { quantityOnHand = Math.addExact(quantityOnHand, quantity); }
	public long getAvailable() { return quantityOnHand - quantityReserved; }
	public UUID getWarehouseId() { return warehouseId; }
	public String getSku() { return sku; }
	public long getQuantityOnHand() { return quantityOnHand; }
	public long getQuantityReserved() { return quantityReserved; }
}
