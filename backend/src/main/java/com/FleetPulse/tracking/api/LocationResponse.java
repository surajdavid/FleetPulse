package com.FleetPulse.tracking.api;

import java.time.Instant;
import java.util.UUID;

import com.FleetPulse.tracking.domain.LocationUpdate;

public record LocationResponse(UUID shipmentId, double latitude, double longitude, Double accuracyMeters,
		Instant recordedAt, Instant receivedAt) {
	public static LocationResponse from(UUID shipmentId, LocationUpdate update) {
		return new LocationResponse(shipmentId, update.getLatitude(), update.getLongitude(), update.getAccuracyMeters(),
				update.getRecordedAt(), update.getReceivedAt());
	}
}
