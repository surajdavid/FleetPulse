package com.FleetPulse.tracking.api;

import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import com.FleetPulse.tracking.application.TrackingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrackingController {
	private final TrackingService tracking;
	public TrackingController(TrackingService tracking) { this.tracking = tracking; }
	@PostMapping("/api/v1/tracking/location-updates")
	public ResponseEntity<LocationResponse> ingest(@Valid @RequestBody LocationUpdateRequest request, HttpServletRequest servletRequest) {
		String correlationId = String.valueOf(servletRequest.getAttribute("X-Correlation-ID"));
		return ResponseEntity.accepted().body(tracking.ingest(request, correlationId));
	}
	@GetMapping("/api/v1/shipments/{shipmentId}/tracking")
	public List<LocationResponse> history(@PathVariable UUID shipmentId) { return tracking.history(shipmentId); }
}
