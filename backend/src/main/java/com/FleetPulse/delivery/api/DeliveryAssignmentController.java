package com.FleetPulse.delivery.api;

import java.net.URI;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import com.FleetPulse.delivery.api.DeliveryRequests.AssignDriverRequest;
import com.FleetPulse.delivery.api.DeliveryRequests.ProofOfDeliveryRequest;
import com.FleetPulse.delivery.api.DeliveryRequests.UpdateAssignmentRequest;
import com.FleetPulse.delivery.api.DeliveryResponses.AssignmentResponse;
import com.FleetPulse.delivery.application.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeliveryAssignmentController {
	private final DeliveryService delivery;
	public DeliveryAssignmentController(DeliveryService delivery) { this.delivery = delivery; }

	@PostMapping("/api/v1/shipments/{shipmentId}/assignments")
	public ResponseEntity<AssignmentResponse> assign(@PathVariable UUID shipmentId, @Valid @RequestBody AssignDriverRequest request,
			@RequestHeader("Idempotency-Key") String key, HttpServletRequest servletRequest) {
		if (key.isBlank() || key.length() > 100) throw new IllegalArgumentException("Invalid idempotency key");
		String correlationId = String.valueOf(servletRequest.getAttribute("X-Correlation-ID"));
		AssignmentResponse response = AssignmentResponse.from(delivery.assign(shipmentId, request.driverId(), key, correlationId));
		return ResponseEntity.created(URI.create("/api/v1/delivery-assignments/" + response.id())).body(response);
	}

	@PatchMapping("/api/v1/delivery-assignments/{assignmentId}/status")
	public AssignmentResponse update(@PathVariable UUID assignmentId, @Valid @RequestBody UpdateAssignmentRequest request) {
		return AssignmentResponse.from(delivery.update(assignmentId, request.status(), request.reason()));
	}

	@PostMapping("/api/v1/shipments/{shipmentId}/proof-of-delivery")
	public AssignmentResponse proof(@PathVariable UUID shipmentId, @Valid @RequestBody ProofOfDeliveryRequest request) {
		return AssignmentResponse.from(delivery.submitProofForShipment(shipmentId, request.objectReference()));
	}
}
