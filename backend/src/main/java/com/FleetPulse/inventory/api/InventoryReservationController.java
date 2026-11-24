package com.FleetPulse.inventory.api;

import java.util.List;

import jakarta.validation.Valid;
import com.FleetPulse.inventory.api.InventoryRequests.ReserveInventoryRequest;
import com.FleetPulse.inventory.api.InventoryResponses.ReservationResponse;
import com.FleetPulse.inventory.application.InventoryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventory/reservations")
public class InventoryReservationController {
	private final InventoryService inventory;
	public InventoryReservationController(InventoryService inventory) { this.inventory = inventory; }
	@PostMapping
	public List<ReservationResponse> reserve(@Valid @RequestBody ReserveInventoryRequest request,
			@RequestHeader("Idempotency-Key") String idempotencyKey) {
		if (idempotencyKey.isBlank() || idempotencyKey.length() > 100) throw new IllegalArgumentException("Invalid idempotency key");
		return inventory.reserve(request, idempotencyKey).stream().map(ReservationResponse::from).toList();
	}
	@DeleteMapping("/{reservationId}")
	public org.springframework.http.ResponseEntity<Void> release(@PathVariable java.util.UUID reservationId) {
		inventory.releaseReservation(reservationId);
		return org.springframework.http.ResponseEntity.noContent().build();
	}
}
