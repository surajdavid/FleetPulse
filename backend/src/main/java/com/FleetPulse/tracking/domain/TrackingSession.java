package com.FleetPulse.tracking.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity @Table(name = "tracking_session")
public class TrackingSession {
	@Id private UUID id;
	@Column(name = "tenant_id", nullable = false, updatable = false) private UUID tenantId;
	@Column(name = "shipment_id", nullable = false, updatable = false) private UUID shipmentId;
	@Column(name = "driver_id", nullable = false, updatable = false) private UUID driverId;
	@Enumerated(EnumType.STRING) @Column(nullable = false, length = 16) private TrackingStatus status;
	@Column(name = "started_at", nullable = false, updatable = false) private Instant startedAt;
	@Column(name = "ended_at") private Instant endedAt;
	protected TrackingSession() { }
	public TrackingSession(UUID tenantId, UUID shipmentId, UUID driverId) {
		this.id = UUID.randomUUID(); this.tenantId = tenantId; this.shipmentId = shipmentId;
		this.driverId = driverId; this.status = TrackingStatus.ACTIVE;
	}
	@PrePersist void onCreate() { startedAt = Instant.now(); }
	public UUID getId() { return id; }
	public UUID getShipmentId() { return shipmentId; }
	public UUID getDriverId() { return driverId; }
	public TrackingStatus getStatus() { return status; }
}
