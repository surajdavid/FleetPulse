package com.FleetPulse.customer.api;

import java.util.UUID;

import com.FleetPulse.customer.domain.AddressType;
import com.FleetPulse.customer.domain.CustomerAddress;

public record CustomerAddressResponse(UUID id, UUID customerId, AddressType addressType, String line1, String line2,
		String city, String state, String postalCode, String countryCode, Double latitude, Double longitude) {
	public static CustomerAddressResponse from(CustomerAddress address) {
		return new CustomerAddressResponse(address.getId(), address.getCustomerId(), address.getAddressType(), address.getLine1(),
				address.getLine2(), address.getCity(), address.getState(), address.getPostalCode(), address.getCountryCode(),
				address.getLatitude(), address.getLongitude());
	}
}
