package com.FleetPulse.order.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_status_history")
public class OrderStatusHistory {
	@Id private UUID id;
	@Column(name = "tenant_id", nullable = false, updatable = false) private UUID tenantId;
	@ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "order_id", nullable = false) private CustomerOrder order;
	@Enumerated(EnumType.STRING) @Column(name = "old_status", length = 32) private OrderStatus oldStatus;
	@Enumerated(EnumType.STRING) @Column(name = "new_status", nullable = false, length = 32) private OrderStatus newStatus;
	@Column(name = "changed_by", nullable = false, length = 200) private String changedBy;
	@Column(length = 500) private String reason;
	@Column(name = "occurred_at", nullable = false, updatable = false) private Instant occurredAt;

	protected OrderStatusHistory() { }
	public OrderStatusHistory(UUID tenantId, OrderStatus oldStatus, OrderStatus newStatus, String changedBy, String reason) {
		this.id = UUID.randomUUID(); this.tenantId = tenantId; this.oldStatus = oldStatus; this.newStatus = newStatus;
		this.changedBy = changedBy; this.reason = reason; this.occurredAt = Instant.now();
	}
	void setOrder(CustomerOrder order) { this.order = order; }
}
