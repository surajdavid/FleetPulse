package com.FleetPulse.delivery.api;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.FleetPulse.delivery.domain.AssignmentStatus;

public final class DeliveryRequests {
	private DeliveryRequests() { }
	public record CreateDriverRequest(@NotBlank @Size(max = 200) String displayName, @NotBlank @Size(max = 200) String contactReference) { }
	public record AssignDriverRequest(@NotNull UUID driverId) { }
	public record UpdateAssignmentRequest(@NotNull AssignmentStatus status, @Size(max = 500) String reason) { }
	public record ProofOfDeliveryRequest(@NotBlank @Size(max = 500) String objectReference) { }
}
