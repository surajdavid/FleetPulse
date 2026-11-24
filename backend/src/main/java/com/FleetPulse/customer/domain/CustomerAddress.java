package com.FleetPulse.customer.domain;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_address")
public class CustomerAddress {
	@Id
	private UUID id;
	@Column(name = "tenant_id", nullable = false, updatable = false)
	private UUID tenantId;
	@Column(name = "customer_id", nullable = false, updatable = false)
	private UUID customerId;
	@Enumerated(EnumType.STRING)
	@Column(name = "address_type", nullable = false, length = 16)
	private AddressType addressType;
	@Column(nullable = false, length = 200)
	private String line1;
	@Column(length = 200)
	private String line2;
	@Column(nullable = false, length = 100)
	private String city;
	@Column(length = 100)
	private String state;
	@Column(name = "postal_code", nullable = false, length = 24)
	private String postalCode;
	@Column(name = "country_code", nullable = false, length = 2)
	private String countryCode;
	private Double latitude;
	private Double longitude;

	protected CustomerAddress() { }

	public CustomerAddress(UUID tenantId, UUID customerId, AddressType addressType, String line1, String line2,
			String city, String state, String postalCode, String countryCode, Double latitude, Double longitude) {
		this.id = UUID.randomUUID();
		this.tenantId = tenantId;
		this.customerId = customerId;
		update(addressType, line1, line2, city, state, postalCode, countryCode, latitude, longitude);
	}

	public void update(AddressType addressType, String line1, String line2, String city, String state,
			String postalCode, String countryCode, Double latitude, Double longitude) {
		this.addressType = addressType;
		this.line1 = line1;
		this.line2 = line2;
		this.city = city;
		this.state = state;
		this.postalCode = postalCode;
		this.countryCode = countryCode.toUpperCase(java.util.Locale.ROOT);
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public UUID getId() { return id; }
	public UUID getCustomerId() { return customerId; }
	public AddressType getAddressType() { return addressType; }
	public String getLine1() { return line1; }
	public String getLine2() { return line2; }
	public String getCity() { return city; }
	public String getState() { return state; }
	public String getPostalCode() { return postalCode; }
	public String getCountryCode() { return countryCode; }
	public Double getLatitude() { return latitude; }
	public Double getLongitude() { return longitude; }
}
