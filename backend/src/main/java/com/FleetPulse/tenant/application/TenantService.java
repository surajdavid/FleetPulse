package com.FleetPulse.tenant.application;

import java.util.UUID;

import com.FleetPulse.platform.tenant.TenantContext;
import com.FleetPulse.tenant.api.TenantRequests.CreateTenantRequest;
import com.FleetPulse.tenant.api.TenantRequests.UpdateTenantStatusRequest;
import com.FleetPulse.tenant.domain.Tenant;
import com.FleetPulse.tenant.domain.TenantMembership;
import com.FleetPulse.tenant.domain.TenantRole;
import com.FleetPulse.tenant.infrastructure.TenantRepository;
import com.FleetPulse.tenant.infrastructure.TenantMembershipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TenantService {
	private final TenantRepository tenants;
	private final TenantMembershipRepository memberships;

	public TenantService(TenantRepository tenants, TenantMembershipRepository memberships) {
		this.tenants = tenants;
		this.memberships = memberships;
	}

	public Tenant create(CreateTenantRequest request) { return tenants.save(new Tenant(request.name().trim())); }

	@Transactional(readOnly = true)
	public Tenant get(UUID id) {
		if (!TenantContext.isPlatformAdministrator() && !TenantContext.requireTenantId().equals(id)) {
			throw new TenantAccessDeniedException();
		}
		return tenants.findById(id).orElseThrow(TenantNotFoundException::new);
	}

	public Tenant updateStatus(UUID id, UpdateTenantStatusRequest request) {
		Tenant tenant = tenants.findById(id).orElseThrow(TenantNotFoundException::new);
		tenant.setStatus(request.status());
		return tenant;
	}

	public TenantMembership addMember(UUID tenantId, UUID userId, TenantRole role) {
		if (!TenantContext.isPlatformAdministrator() && !TenantContext.requireTenantId().equals(tenantId)) {
			throw new TenantAccessDeniedException();
		}
		if (!tenants.existsById(tenantId)) throw new TenantNotFoundException();
		if (memberships.existsByTenantIdAndUserId(tenantId, userId)) throw new TenantMembershipAlreadyExistsException();
		return memberships.save(new TenantMembership(tenantId, userId, role));
	}
}
