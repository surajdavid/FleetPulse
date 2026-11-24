package com.FleetPulse.tenant.infrastructure;

import java.util.UUID;

import com.FleetPulse.tenant.domain.TenantMembership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantMembershipRepository extends JpaRepository<TenantMembership, UUID> {
	boolean existsByTenantIdAndUserId(UUID tenantId, UUID userId);
}
