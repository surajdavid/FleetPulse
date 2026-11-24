package com.FleetPulse.inventory.infrastructure;

import java.util.List;
import java.util.UUID;

import com.FleetPulse.inventory.domain.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, UUID> {
	List<InventoryReservation> findAllByTenantIdAndIdempotencyKey(UUID tenantId, String idempotencyKey);
	java.util.Optional<InventoryReservation> findByTenantIdAndId(UUID tenantId, UUID id);
}
