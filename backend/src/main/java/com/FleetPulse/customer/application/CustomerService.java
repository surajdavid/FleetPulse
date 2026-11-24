package com.FleetPulse.customer.application;

import java.util.UUID;

import com.FleetPulse.customer.api.CustomerRequests.CreateCustomerRequest;
import com.FleetPulse.customer.api.CustomerRequests.UpdateCustomerRequest;
import com.FleetPulse.customer.domain.Customer;
import com.FleetPulse.customer.infrastructure.CustomerRepository;
import com.FleetPulse.platform.tenant.TenantContext;
import com.FleetPulse.tenant.application.TenantAccessGuard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerService {
	private final CustomerRepository customers;
	private final TenantAccessGuard tenantAccess;

	public CustomerService(CustomerRepository customers, TenantAccessGuard tenantAccess) {
		this.customers = customers;
		this.tenantAccess = tenantAccess;
	}

	public Customer create(CreateCustomerRequest request) {
		UUID tenantId = TenantContext.requireTenantId();
		tenantAccess.requireActive(tenantId);
		return customers.save(new Customer(tenantId, request.customerCode(), request.name(), request.email(), request.phone()));
	}

	@Transactional(readOnly = true)
	public Page<Customer> list(int page, int size) {
		UUID tenantId = TenantContext.requireTenantId();
		tenantAccess.requireActive(tenantId);
		return customers.findAllByTenantId(tenantId, PageRequest.of(page, size, Sort.by("createdAt").descending()));
	}

	@Transactional(readOnly = true)
	public Customer get(UUID id) { tenantAccess.requireActive(TenantContext.requireTenantId()); return find(id); }

	public Customer update(UUID id, UpdateCustomerRequest request) {
		tenantAccess.requireActive(TenantContext.requireTenantId());
		Customer customer = find(id);
		customer.update(request.name(), request.email(), request.phone());
		return customer;
	}

	private Customer find(UUID id) {
		return customers.findByTenantIdAndId(TenantContext.requireTenantId(), id).orElseThrow(CustomerNotFoundException::new);
	}
}
