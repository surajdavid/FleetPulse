package com.FleetPulse.delivery.infrastructure;

import java.util.Optional;
import java.util.UUID;

import com.FleetPulse.delivery.domain.AssignmentStatus;
import com.FleetPulse.delivery.domain.DeliveryAssignment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeliveryAssignmentRepository extends JpaRepository<DeliveryAssignment, UUID> {
	Optional<DeliveryAssignment> findByTenantIdAndIdempotencyKey(UUID tenantId, String key);
	@Query("select a from DeliveryAssignment a where a.tenantId = :tenantId and a.shipmentId = :shipmentId and a.status in :statuses")
	java.util.List<DeliveryAssignment> findByShipmentAndStatuses(@Param("tenantId") UUID tenantId, @Param("shipmentId") UUID shipmentId,
			@Param("statuses") java.util.Collection<AssignmentStatus> statuses);
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<DeliveryAssignment> findByTenantIdAndId(UUID tenantId, UUID id);
	@Query("select count(a) > 0 from DeliveryAssignment a where a.tenantId = :tenantId and a.shipmentId = :shipmentId and a.status in :statuses")
	boolean hasActiveAssignment(@Param("tenantId") UUID tenantId, @Param("shipmentId") UUID shipmentId,
			@Param("statuses") java.util.Collection<AssignmentStatus> statuses);
	boolean existsByTenantIdAndShipmentIdAndDriverIdAndStatusIn(UUID tenantId, UUID shipmentId, UUID driverId,
			java.util.Collection<AssignmentStatus> statuses);
}
