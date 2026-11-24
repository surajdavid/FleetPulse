package com.FleetPulse.order.domain;

import java.time.Instant;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;

@Entity
@Table(name = "shipment")
public class Shipment {
	@Id private UUID id;
	@Column(name = "tenant_id", nullable = false, updatable = false) private UUID tenantId;
	@Column(name = "shipment_number", nullable = false, length = 32, updatable = false) private String shipmentNumber;
	@ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "order_id", nullable = false) private CustomerOrder order;
	@Column(name = "origin_warehouse_id") private UUID originWarehouseId;
	@Column(name = "destination_address_snapshot", nullable = false, columnDefinition = "TEXT") private String destinationAddressSnapshot;
	@Enumerated(EnumType.STRING) @Column(nullable = false, length = 32) private ShipmentStatus status;
	@Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
	@Column(name = "updated_at", nullable = false) private Instant updatedAt;
	@Version private long version;
	@OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true) private Set<ShipmentStatusHistory> statusHistory = new HashSet<>();

	protected Shipment() { }
	public Shipment(UUID tenantId, String shipmentNumber, UUID originWarehouseId, String destinationAddressSnapshot) {
		this.id = UUID.randomUUID(); this.tenantId = tenantId; this.shipmentNumber = shipmentNumber;
		this.originWarehouseId = originWarehouseId; this.destinationAddressSnapshot = destinationAddressSnapshot;
		this.status = ShipmentStatus.CREATED;
	}
	void setOrder(CustomerOrder order) { this.order = order; }
	public void addInitialStatus(String actor) { addStatusHistory(new ShipmentStatusHistory(tenantId, null, status, actor, "Shipment created")); }
	private void addStatusHistory(ShipmentStatusHistory item) { statusHistory.add(item); item.setShipment(this); }
	public void transitionTo(ShipmentStatus next, String actor, String reason) {
		ShipmentStatus previous = status;
		boolean allowed = switch (status) {
			case CREATED -> next == ShipmentStatus.READY_FOR_DISPATCH || next == ShipmentStatus.CANCELLED;
			case READY_FOR_DISPATCH -> next == ShipmentStatus.DISPATCHED || next == ShipmentStatus.CANCELLED;
			case DISPATCHED -> next == ShipmentStatus.IN_TRANSIT || next == ShipmentStatus.DELIVERY_FAILED;
			case IN_TRANSIT -> next == ShipmentStatus.DELIVERED || next == ShipmentStatus.DELIVERY_FAILED || next == ShipmentStatus.RETURN_INITIATED;
			case DELIVERY_FAILED -> next == ShipmentStatus.IN_TRANSIT || next == ShipmentStatus.RETURN_INITIATED;
			case RETURN_INITIATED -> next == ShipmentStatus.RETURNED;
			case DELIVERED, CANCELLED, RETURNED -> false;
		};
		if (!allowed) throw new InvalidShipmentTransitionException(status, next);
		status = next;
		addStatusHistory(new ShipmentStatusHistory(tenantId, previous, next, actor, reason));
	}
	@PrePersist void onCreate() { Instant now = Instant.now(); createdAt = now; updatedAt = now; }
	@PreUpdate void onUpdate() { updatedAt = Instant.now(); }
	public UUID getId() { return id; }
	public String getShipmentNumber() { return shipmentNumber; }
	public UUID getOriginWarehouseId() { return originWarehouseId; }
	public String getDestinationAddressSnapshot() { return destinationAddressSnapshot; }
	public ShipmentStatus getStatus() { return status; }
	public Instant getCreatedAt() { return createdAt; }
}
