package com.FleetPulse.delivery.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.FleetPulse.delivery.domain.Driver;
import com.FleetPulse.delivery.domain.DriverStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface DriverRepository extends JpaRepository<Driver, UUID> {
	List<Driver> findAllByTenantIdAndStatus(UUID tenantId, DriverStatus status);
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<Driver> findByTenantIdAndId(UUID tenantId, UUID id);
}
