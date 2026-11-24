package com.FleetPulse.customer.application;

import java.util.List;
import java.util.UUID;

import com.FleetPulse.customer.api.AddressRequests.CreateAddressRequest;
import com.FleetPulse.customer.api.AddressRequests.UpdateAddressRequest;
import com.FleetPulse.customer.domain.CustomerAddress;
import com.FleetPulse.customer.infrastructure.CustomerAddressRepository;
import com.FleetPulse.customer.infrastructure.CustomerRepository;
import com.FleetPulse.platform.tenant.TenantContext;
import com.FleetPulse.tenant.application.TenantAccessGuard;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerAddressService {
	private final CustomerRepository customers;
	private final CustomerAddressRepository addresses;
	private final TenantAccessGuard tenantAccess;

	public CustomerAddressService(CustomerRepository customers, CustomerAddressRepository addresses, TenantAccessGuard tenantAccess) {
		this.customers = customers;
		this.addresses = addresses;
		this.tenantAccess = tenantAccess;
	}

	public CustomerAddress create(UUID customerId, CreateAddressRequest request) {
		validateCoordinates(request.latitude(), request.longitude());
		UUID tenantId = TenantContext.requireTenantId();
		tenantAccess.requireActive(tenantId);
		customers.findByTenantIdAndId(tenantId, customerId).orElseThrow(CustomerNotFoundException::new);
		return addresses.save(new CustomerAddress(tenantId, customerId, request.addressType(), request.line1(), request.line2(),
				request.city(), request.state(), request.postalCode(), request.countryCode(), request.latitude(), request.longitude()));
	}

	@Transactional(readOnly = true)
	public List<CustomerAddress> list(UUID customerId) {
		UUID tenantId = TenantContext.requireTenantId();
		tenantAccess.requireActive(tenantId);
		customers.findByTenantIdAndId(tenantId, customerId).orElseThrow(CustomerNotFoundException::new);
		return addresses.findAllByTenantIdAndCustomerId(tenantId, customerId);
	}

	public CustomerAddress update(UUID customerId, UUID addressId, UpdateAddressRequest request) {
		validateCoordinates(request.latitude(), request.longitude());
		CustomerAddress address = find(customerId, addressId);
		address.update(request.addressType(), request.line1(), request.line2(), request.city(), request.state(),
				request.postalCode(), request.countryCode(), request.latitude(), request.longitude());
		return address;
	}

	@Transactional(readOnly = true)
	public CustomerAddress get(UUID customerId, UUID addressId) { return find(customerId, addressId); }

	public void delete(UUID customerId, UUID addressId) { addresses.delete(find(customerId, addressId)); }

	private CustomerAddress find(UUID customerId, UUID addressId) {
		UUID tenantId = TenantContext.requireTenantId();
		tenantAccess.requireActive(tenantId);
		customers.findByTenantIdAndId(tenantId, customerId).orElseThrow(CustomerNotFoundException::new);
		return addresses.findByTenantIdAndCustomerIdAndId(tenantId, customerId, addressId)
				.orElseThrow(CustomerAddressNotFoundException::new);
	}

	private void validateCoordinates(Double latitude, Double longitude) {
		if ((latitude == null) != (longitude == null)) throw new IllegalArgumentException("Latitude and longitude must be supplied together");
	}
}
