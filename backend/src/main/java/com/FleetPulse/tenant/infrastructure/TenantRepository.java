package com.FleetPulse.tenant.infrastructure;

import java.util.UUID;

import com.FleetPulse.tenant.domain.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, UUID> { }
