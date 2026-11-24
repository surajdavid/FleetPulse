package com.FleetPulse.customer.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class CustomerRequests {
	private CustomerRequests() { }

	public record CreateCustomerRequest(
			@NotBlank @Size(max = 64) @Pattern(regexp = "[A-Za-z0-9._-]+") String customerCode,
			@NotBlank @Size(max = 200) String name,
			@Email @Size(max = 320) String email,
			@Size(max = 32) String phone) { }

	public record UpdateCustomerRequest(
			@Size(min = 1, max = 200) String name,
			@Email @Size(max = 320) String email,
			@Size(max = 32) String phone) { }
}
