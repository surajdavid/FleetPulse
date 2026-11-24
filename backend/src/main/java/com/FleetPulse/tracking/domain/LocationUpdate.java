package com.FleetPulse.tracking.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name = "location_update")
public class LocationUpdate {
	@Id private UUID id;
	@Column(name = "tenant_id", nullable = false, updatable = false) private UUID tenantId;
	@Column(name = "tracking_session_id", nullable = false, updatable = false) private UUID trackingSessionId;
	@Column(nullable = false) private double latitude;
	@Column(nullable = false) private double longitude;
	@Column(name = "accuracy_m") private Double accuracyMeters;
	@Column(name = "recorded_at", nullable = false, updatable = false) private Instant recordedAt;
	@Column(name = "received_at", nullable = false, updatable = false) private Instant receivedAt;
	protected LocationUpdate() { }
	public LocationUpdate(UUID tenantId, UUID sessionId, double latitude, double longitude, Double accuracyMeters, Instant recordedAt) {
		this.id = UUID.randomUUID(); this.tenantId = tenantId; this.trackingSessionId = sessionId;
		this.latitude = latitude; this.longitude = longitude; this.accuracyMeters = accuracyMeters;
		this.recordedAt = recordedAt; this.receivedAt = Instant.now();
	}
	public UUID getId() { return id; }
	public double getLatitude() { return latitude; }
	public double getLongitude() { return longitude; }
	public Double getAccuracyMeters() { return accuracyMeters; }
	public Instant getRecordedAt() { return recordedAt; }
	public Instant getReceivedAt() { return receivedAt; }
}
