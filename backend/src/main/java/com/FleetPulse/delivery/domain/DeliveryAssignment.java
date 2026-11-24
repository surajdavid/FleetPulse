package com.FleetPulse.delivery.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

@Entity
@Table(name = "delivery_assignment", uniqueConstraints = {
		@UniqueConstraint(name = "uk_assignment_tenant_idempotency", columnNames = {"tenant_id", "idempotency_key"})})
public class DeliveryAssignment {
	@Id private UUID id;
	@Column(name = "tenant_id", nullable = false, updatable = false) private UUID tenantId;
	@Column(name = "shipment_id", nullable = false, updatable = false) private UUID shipmentId;
	@Column(name = "driver_id", nullable = false, updatable = false) private UUID driverId;
	@Enumerated(EnumType.STRING) @Column(nullable = false, length = 24) private AssignmentStatus status;
	@Column(name = "idempotency_key", nullable = false, length = 100, updatable = false) private String idempotencyKey;
	@Column(name = "assigned_at", nullable = false, updatable = false) private Instant assignedAt;
	@Column(name = "accepted_at") private Instant acceptedAt;
	@Column(name = "completed_at") private Instant completedAt;
	@Column(name = "proof_of_delivery_reference", length = 500) private String proofOfDeliveryReference;
	@Column(name = "failure_reason", length = 500) private String failureReason;
	@Version private long version;
	@Column(name = "created_at", nullable = false) private Instant createdAt;
	@Column(name = "updated_at", nullable = false) private Instant updatedAt;
	protected DeliveryAssignment() { }
	public DeliveryAssignment(UUID tenantId, UUID shipmentId, UUID driverId, String key) {
		this.id = UUID.randomUUID(); this.tenantId = tenantId; this.shipmentId = shipmentId; this.driverId = driverId;
		this.idempotencyKey = key; this.status = AssignmentStatus.ASSIGNED;
	}
	public void transitionTo(AssignmentStatus next, String reason) {
		boolean allowed = switch (status) {
			case ASSIGNED -> next == AssignmentStatus.ACCEPTED || next == AssignmentStatus.REJECTED;
			case ACCEPTED -> next == AssignmentStatus.PICKED_UP || next == AssignmentStatus.FAILED || next == AssignmentStatus.RESCHEDULED;
			case PICKED_UP -> next == AssignmentStatus.IN_TRANSIT || next == AssignmentStatus.FAILED;
			case IN_TRANSIT -> next == AssignmentStatus.OUT_FOR_DELIVERY || next == AssignmentStatus.FAILED || next == AssignmentStatus.RETURNED;
			case OUT_FOR_DELIVERY -> next == AssignmentStatus.DELIVERED || next == AssignmentStatus.FAILED || next == AssignmentStatus.RESCHEDULED;
			case FAILED -> next == AssignmentStatus.RESCHEDULED || next == AssignmentStatus.RETURNED;
			case RESCHEDULED -> next == AssignmentStatus.ASSIGNED || next == AssignmentStatus.RETURNED;
			case DELIVERED, REJECTED, RETURNED -> false;
		};
		if (!allowed) throw new InvalidAssignmentTransitionException(status, next);
		status = next;
		if (next == AssignmentStatus.ACCEPTED) acceptedAt = Instant.now();
		if (next == AssignmentStatus.DELIVERED || next == AssignmentStatus.RETURNED) completedAt = Instant.now();
		if (next == AssignmentStatus.FAILED) failureReason = reason;
	}
	public void submitProof(String reference) {
		if (status != AssignmentStatus.OUT_FOR_DELIVERY && status != AssignmentStatus.DELIVERED) throw new InvalidAssignmentTransitionException(status, AssignmentStatus.DELIVERED);
		proofOfDeliveryReference = reference;
	}
	@PrePersist void onCreate() { Instant now = Instant.now(); assignedAt = now; createdAt = now; updatedAt = now; }
	@PreUpdate void onUpdate() { updatedAt = Instant.now(); }
	public UUID getId() { return id; }
	public UUID getShipmentId() { return shipmentId; }
	public UUID getDriverId() { return driverId; }
	public AssignmentStatus getStatus() { return status; }
	public Instant getAssignedAt() { return assignedAt; }
	public Instant getAcceptedAt() { return acceptedAt; }
	public Instant getCompletedAt() { return completedAt; }
	public String getProofOfDeliveryReference() { return proofOfDeliveryReference; }
}
