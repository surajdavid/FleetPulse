package com.FleetPulse.tenant.api;

import java.time.Instant;
import java.util.UUID;

import com.FleetPulse.tenant.domain.Tenant;
import com.FleetPulse.tenant.domain.TenantStatus;
import com.FleetPulse.tenant.domain.TenantMembership;
import com.FleetPulse.tenant.domain.TenantRole;
import com.FleetPulse.tenant.domain.MembershipStatus;

public record TenantResponse(UUID id, String name, TenantStatus status, Instant createdAt, Instant updatedAt) {
	public static TenantResponse from(Tenant tenant) {
		return new TenantResponse(tenant.getId(), tenant.getName(), tenant.getStatus(), tenant.getCreatedAt(), tenant.getUpdatedAt());
	}
	public record MemberResponse(UUID id, UUID tenantId, UUID userId, TenantRole role, MembershipStatus status, Instant createdAt) {
		public static MemberResponse from(TenantMembership member) {
			return new MemberResponse(member.getId(), member.getTenantId(), member.getUserId(), member.getRole(), member.getStatus(), member.getCreatedAt());
		}
	}
}
