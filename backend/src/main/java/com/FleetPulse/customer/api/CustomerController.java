package com.FleetPulse.customer.api;

import java.net.URI;
import java.util.UUID;

import jakarta.validation.Valid;
import com.FleetPulse.customer.application.CustomerService;
import com.FleetPulse.customer.api.CustomerRequests.CreateCustomerRequest;
import com.FleetPulse.customer.api.CustomerRequests.UpdateCustomerRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
	private final CustomerService customers;

	public CustomerController(CustomerService customers) { this.customers = customers; }

	@PostMapping
	public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CreateCustomerRequest request) {
		CustomerResponse response = CustomerResponse.from(customers.create(request));
		return ResponseEntity.created(URI.create("/api/v1/customers/" + response.id())).body(response);
	}

	@GetMapping
	public Page<CustomerResponse> list(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
		if (page < 0 || size < 1 || size > 100) throw new IllegalArgumentException("Invalid pagination");
		return customers.list(page, size).map(CustomerResponse::from);
	}

	@GetMapping("/{customerId}")
	public CustomerResponse get(@PathVariable UUID customerId) { return CustomerResponse.from(customers.get(customerId)); }

	@PatchMapping("/{customerId}")
	public CustomerResponse update(@PathVariable UUID customerId, @Valid @RequestBody UpdateCustomerRequest request) {
		return CustomerResponse.from(customers.update(customerId, request));
	}
}
