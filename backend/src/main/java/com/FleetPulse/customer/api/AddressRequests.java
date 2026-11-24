package com.FleetPulse.customer.api;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import com.FleetPulse.customer.domain.AddressType;

public final class AddressRequests {
	private AddressRequests() { }

	public record CreateAddressRequest(@NotNull AddressType addressType, @NotBlank @Size(max = 200) String line1,
			@Size(max = 200) String line2, @NotBlank @Size(max = 100) String city, @Size(max = 100) String state,
			@NotBlank @Size(max = 24) String postalCode, @NotBlank @Pattern(regexp = "[A-Za-z]{2}") String countryCode,
			@DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
			@DecimalMin("-180.0") @DecimalMax("180.0") Double longitude) { }

	public record UpdateAddressRequest(@NotNull AddressType addressType, @NotBlank @Size(max = 200) String line1,
			@Size(max = 200) String line2, @NotBlank @Size(max = 100) String city, @Size(max = 100) String state,
			@NotBlank @Size(max = 24) String postalCode, @NotBlank @Pattern(regexp = "[A-Za-z]{2}") String countryCode,
			@DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
			@DecimalMin("-180.0") @DecimalMax("180.0") Double longitude) { }
}
