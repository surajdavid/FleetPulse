package com.FleetPulse.tracking.infrastructure;

import java.util.Optional;
import java.util.UUID;

import com.FleetPulse.tracking.domain.TrackingSession;
import com.FleetPulse.tracking.domain.TrackingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackingSessionRepository extends JpaRepository<TrackingSession, UUID> {
	Optional<TrackingSession> findFirstByTenantIdAndShipmentIdAndStatusOrderByStartedAtDesc(UUID tenantId, UUID shipmentId, TrackingStatus status);
}
