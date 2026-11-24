package com.FleetPulse.order.domain;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity @Table(name = "order_item")
public class OrderItem {
	@Id private UUID id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "order_id", nullable = false) private CustomerOrder order;
	@Column(name = "tenant_id", nullable = false, updatable = false) private UUID tenantId;
	@Column(nullable = false, length = 100) private String sku;
	@Column(nullable = false, length = 300) private String description;
	@Column(nullable = false) private int quantity;
	@Column(name = "unit_price", nullable = false, precision = 19, scale = 4) private BigDecimal unitPrice;
	@Column(name = "line_total", nullable = false, precision = 19, scale = 4) private BigDecimal lineTotal;
	protected OrderItem() { }
	public OrderItem(UUID tenantId, String sku, String description, int quantity, BigDecimal unitPrice) {
		this.id = UUID.randomUUID(); this.tenantId = tenantId; this.sku = sku; this.description = description;
		this.quantity = quantity; this.unitPrice = unitPrice; this.lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
	}
	void setOrder(CustomerOrder order) { this.order = order; }
	public String getSku() { return sku; }
	public String getDescription() { return description; }
	public int getQuantity() { return quantity; }
	public BigDecimal getUnitPrice() { return unitPrice; }
	public BigDecimal getLineTotal() { return lineTotal; }
}
