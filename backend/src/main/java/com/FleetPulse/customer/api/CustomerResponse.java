package com.FleetPulse.customer.api;

import java.time.Instant;
import java.util.UUID;

import com.FleetPulse.customer.domain.Customer;
import com.FleetPulse.customer.domain.CustomerStatus;

public record CustomerResponse(UUID id, String customerCode, String name, String email, String phone,
		CustomerStatus status, Instant createdAt) {
	public static CustomerResponse from(Customer customer) {
		return new CustomerResponse(customer.getId(), customer.getCustomerCode(), customer.getName(), customer.getEmail(),
				customer.getPhone(), customer.getStatus(), customer.getCreatedAt());
	}
}
