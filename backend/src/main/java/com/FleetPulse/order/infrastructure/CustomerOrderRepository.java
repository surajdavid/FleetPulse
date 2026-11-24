package com.FleetPulse.order.infrastructure;

import java.util.Optional;
import java.util.UUID;

import com.FleetPulse.order.domain.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, UUID> {
	@EntityGraph(attributePaths = {"items", "shipments"})
	Optional<CustomerOrder> findByTenantIdAndId(UUID tenantId, UUID id);
	@EntityGraph(attributePaths = {"items", "shipments"})
	Optional<CustomerOrder> findByTenantIdAndIdempotencyKey(UUID tenantId, String idempotencyKey);
}
