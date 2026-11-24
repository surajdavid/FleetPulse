package com.FleetPulse.delivery.api;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import com.FleetPulse.delivery.api.DeliveryRequests.CreateDriverRequest;
import com.FleetPulse.delivery.api.DeliveryResponses.DriverResponse;
import com.FleetPulse.delivery.application.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/delivery-partners")
public class DriverController {
	private final DeliveryService delivery;
	public DriverController(DeliveryService delivery) { this.delivery = delivery; }
	@GetMapping("/available")
	public List<DriverResponse> available() { return delivery.availableDrivers().stream().map(DriverResponse::from).toList(); }
	@PostMapping
	public ResponseEntity<DriverResponse> create(@Valid @RequestBody CreateDriverRequest request) {
		DriverResponse response = DriverResponse.from(delivery.createDriver(request));
		return ResponseEntity.created(URI.create("/api/v1/delivery-partners/" + response.id())).body(response);
	}
}
