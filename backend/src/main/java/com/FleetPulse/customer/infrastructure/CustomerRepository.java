package com.FleetPulse.customer.infrastructure;

import java.util.Optional;
import java.util.UUID;

import com.FleetPulse.customer.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
	Optional<Customer> findByTenantIdAndId(UUID tenantId, UUID id);
	Page<Customer> findAllByTenantId(UUID tenantId, Pageable pageable);
}
