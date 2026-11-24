package com.FleetPulse.inventory.infrastructure;

import java.util.Optional;
import java.util.UUID;

import com.FleetPulse.inventory.domain.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {
	Optional<Warehouse> findByTenantIdAndId(UUID tenantId, UUID id);
	boolean existsByTenantIdAndCode(UUID tenantId, String code);
}
