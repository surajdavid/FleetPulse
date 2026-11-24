package com.FleetPulse.customer.api;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import com.FleetPulse.customer.api.AddressRequests.CreateAddressRequest;
import com.FleetPulse.customer.api.AddressRequests.UpdateAddressRequest;
import com.FleetPulse.customer.application.CustomerAddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/addresses")
public class CustomerAddressController {
	private final CustomerAddressService addresses;

	public CustomerAddressController(CustomerAddressService addresses) { this.addresses = addresses; }

	@PostMapping
	public ResponseEntity<CustomerAddressResponse> create(@PathVariable UUID customerId,
			@Valid @RequestBody CreateAddressRequest request) {
		CustomerAddressResponse response = CustomerAddressResponse.from(addresses.create(customerId, request));
		return ResponseEntity.created(URI.create("/api/v1/customers/" + customerId + "/addresses/" + response.id())).body(response);
	}

	@GetMapping
	public List<CustomerAddressResponse> list(@PathVariable UUID customerId) {
		return addresses.list(customerId).stream().map(CustomerAddressResponse::from).toList();
	}

	@PatchMapping("/{addressId}")
	public CustomerAddressResponse update(@PathVariable UUID customerId, @PathVariable UUID addressId,
			@Valid @RequestBody UpdateAddressRequest request) {
		return CustomerAddressResponse.from(addresses.update(customerId, addressId, request));
	}

	@DeleteMapping("/{addressId}")
	public ResponseEntity<Void> delete(@PathVariable UUID customerId, @PathVariable UUID addressId) {
		addresses.delete(customerId, addressId);
		return ResponseEntity.noContent().build();
	}
}
