package com.FleetPulse.delivery.api;

import java.time.Instant;
import java.util.UUID;

import com.FleetPulse.delivery.domain.AssignmentStatus;
import com.FleetPulse.delivery.domain.DeliveryAssignment;
import com.FleetPulse.delivery.domain.Driver;
import com.FleetPulse.delivery.domain.DriverStatus;

public final class DeliveryResponses {
	private DeliveryResponses() { }
	public record DriverResponse(UUID id, String displayName, DriverStatus status) {
		public static DriverResponse from(Driver driver) { return new DriverResponse(driver.getId(), driver.getDisplayName(), driver.getStatus()); }
	}
	public record AssignmentResponse(UUID id, UUID shipmentId, UUID driverId, AssignmentStatus status, Instant assignedAt,
		Instant acceptedAt, Instant completedAt, String proofOfDeliveryReference) {
		public static AssignmentResponse from(DeliveryAssignment assignment) {
			return new AssignmentResponse(assignment.getId(), assignment.getShipmentId(), assignment.getDriverId(), assignment.getStatus(),
					assignment.getAssignedAt(), assignment.getAcceptedAt(), assignment.getCompletedAt(), assignment.getProofOfDeliveryReference());
		}
	}
}
