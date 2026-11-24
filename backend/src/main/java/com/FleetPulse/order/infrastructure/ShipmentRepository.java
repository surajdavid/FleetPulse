package com.FleetPulse.order.infrastructure;

import java.util.Optional;
import java.util.UUID;

import com.FleetPulse.order.domain.Shipment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {
	@EntityGraph(attributePaths = {"order", "order.items", "order.shipments"})
	Optional<Shipment> findByTenantIdAndId(UUID tenantId, UUID id);
}
