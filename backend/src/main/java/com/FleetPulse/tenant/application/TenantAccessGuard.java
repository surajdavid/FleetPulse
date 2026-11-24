package com.FleetPulse.tenant.application;

import java.util.UUID;

import com.FleetPulse.tenant.domain.TenantStatus;
import com.FleetPulse.tenant.infrastructure.TenantRepository;
import org.springframework.stereotype.Component;

@Component
public class TenantAccessGuard {
	private final TenantRepository tenants;
	public TenantAccessGuard(TenantRepository tenants) { this.tenants = tenants; }
	public void requireActive(UUID tenantId) {
		if (tenants.findById(tenantId).map(tenant -> tenant.getStatus() == TenantStatus.ACTIVE).orElse(false)) return;
		throw new TenantSuspendedException();
	}
}
