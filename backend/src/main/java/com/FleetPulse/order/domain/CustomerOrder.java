package com.FleetPulse.order.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

@Entity
@Table(name = "customer_order", uniqueConstraints = {
		@UniqueConstraint(name = "uk_order_tenant_number", columnNames = {"tenant_id", "order_number"}),
		@UniqueConstraint(name = "uk_order_tenant_idempotency", columnNames = {"tenant_id", "idempotency_key"})})
public class CustomerOrder {
	@Id private UUID id;
	@Column(name = "tenant_id", nullable = false, updatable = false) private UUID tenantId;
	@Column(name = "order_number", nullable = false, length = 32, updatable = false) private String orderNumber;
	@Column(name = "customer_id", nullable = false, updatable = false) private UUID customerId;
	@Enumerated(EnumType.STRING) @Column(nullable = false, length = 32) private OrderStatus status;
	@Column(nullable = false, length = 3, updatable = false) private String currency;
	@Column(nullable = false, precision = 19, scale = 4, updatable = false) private BigDecimal subtotal;
	@Column(nullable = false, precision = 19, scale = 4, updatable = false) private BigDecimal tax;
	@Column(nullable = false, precision = 19, scale = 4, updatable = false) private BigDecimal total;
	@Column(name = "idempotency_key", nullable = false, length = 100, updatable = false) private String idempotencyKey;
	@Column(name = "request_hash", nullable = false, length = 64, updatable = false) private String requestHash;
	@Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
	@Column(name = "updated_at", nullable = false) private Instant updatedAt;
	@Version private long version;
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) private Set<OrderItem> items = new HashSet<>();
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) private Set<Shipment> shipments = new HashSet<>();
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) private Set<OrderStatusHistory> statusHistory = new HashSet<>();

	protected CustomerOrder() { }
	public CustomerOrder(UUID tenantId, String orderNumber, UUID customerId, String currency, BigDecimal subtotal,
			BigDecimal tax, String idempotencyKey, String requestHash) {
		this.id = UUID.randomUUID(); this.tenantId = tenantId; this.orderNumber = orderNumber; this.customerId = customerId;
		this.status = OrderStatus.CREATED; this.currency = currency; this.subtotal = subtotal; this.tax = tax;
		this.total = subtotal.add(tax); this.idempotencyKey = idempotencyKey; this.requestHash = requestHash;
	}
	public void addItem(OrderItem item) { items.add(item); item.setOrder(this); }
	public void addShipment(Shipment shipment) { shipments.add(shipment); shipment.setOrder(this); }
	public void addStatusHistory(OrderStatusHistory item) { statusHistory.add(item); item.setOrder(this); }
	public boolean matchesRequest(String hash) { return requestHash.equals(hash); }
	public void transitionTo(OrderStatus next, String actor, String reason) {
		OrderStatus previous = status;
		if (!allowed(status, next)) throw new InvalidOrderTransitionException(status, next);
		status = next;
		addStatusHistory(new OrderStatusHistory(tenantId, previous, next, actor, reason));
	}
	private static boolean allowed(OrderStatus current, OrderStatus next) {
		return switch (current) {
			case CREATED -> next == OrderStatus.PAYMENT_PENDING || next == OrderStatus.CANCELLED;
			case PAYMENT_PENDING -> next == OrderStatus.CONFIRMED || next == OrderStatus.PAYMENT_FAILED || next == OrderStatus.CANCELLED;
			case CONFIRMED -> next == OrderStatus.PROCESSING || next == OrderStatus.CANCELLED;
			case PROCESSING -> next == OrderStatus.READY_FOR_DISPATCH || next == OrderStatus.CANCELLED;
			case READY_FOR_DISPATCH -> next == OrderStatus.DISPATCHED || next == OrderStatus.CANCELLED;
			case DISPATCHED -> next == OrderStatus.IN_TRANSIT || next == OrderStatus.DELIVERY_FAILED;
			case IN_TRANSIT -> next == OrderStatus.DELIVERED || next == OrderStatus.DELIVERY_FAILED || next == OrderStatus.RETURN_INITIATED;
			case DELIVERY_FAILED -> next == OrderStatus.IN_TRANSIT || next == OrderStatus.RETURN_INITIATED;
			case RETURN_INITIATED -> next == OrderStatus.RETURNED;
			case PAYMENT_FAILED, CANCELLED, DELIVERED, RETURNED -> false;
		};
	}
	@PrePersist void onCreate() { Instant now = Instant.now(); createdAt = now; updatedAt = now; }
	@PreUpdate void onUpdate() { updatedAt = Instant.now(); }
	public UUID getId() { return id; }
	public UUID getTenantId() { return tenantId; }
	public String getOrderNumber() { return orderNumber; }
	public UUID getCustomerId() { return customerId; }
	public OrderStatus getStatus() { return status; }
	public String getCurrency() { return currency; }
	public BigDecimal getSubtotal() { return subtotal; }
	public BigDecimal getTax() { return tax; }
	public BigDecimal getTotal() { return total; }
	public String getIdempotencyKey() { return idempotencyKey; }
	public String getRequestHash() { return requestHash; }
	public Instant getCreatedAt() { return createdAt; }
	public List<OrderItem> getItems() { return items.stream().toList(); }
	public List<Shipment> getShipments() { return shipments.stream().toList(); }
}
