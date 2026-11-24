package com.FleetPulse.order.api;

import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.FleetPulse.order.application.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shipments")
public class ShipmentController {
	private final OrderService orders;
	public ShipmentController(OrderService orders) { this.orders = orders; }
	@PostMapping("/{shipmentId}/cancel")
	public ResponseEntity<Void> cancel(@PathVariable UUID shipmentId, @Valid @RequestBody CancelShipmentRequest request) {
		orders.cancelShipment(shipmentId, request.reason());
		return ResponseEntity.accepted().build();
	}
	public record CancelShipmentRequest(@NotBlank @Size(max = 500) String reason) { }
}
