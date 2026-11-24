package com.FleetPulse.tracking.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.FleetPulse.tracking.domain.LocationUpdate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationUpdateRepository extends JpaRepository<LocationUpdate, UUID> {
	Optional<LocationUpdate> findFirstByTenantIdAndTrackingSessionIdOrderByRecordedAtDesc(UUID tenantId, UUID sessionId);
	List<LocationUpdate> findByTenantIdAndTrackingSessionIdOrderByRecordedAtDesc(UUID tenantId, UUID sessionId, Pageable pageable);
}
