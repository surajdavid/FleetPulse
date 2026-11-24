package com.FleetPulse.customer.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.FleetPulse.customer.domain.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, UUID> {
	List<CustomerAddress> findAllByTenantIdAndCustomerId(UUID tenantId, UUID customerId);
	Optional<CustomerAddress> findByTenantIdAndCustomerIdAndId(UUID tenantId, UUID customerId, UUID id);
}
