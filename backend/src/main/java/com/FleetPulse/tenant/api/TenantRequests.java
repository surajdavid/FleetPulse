package com.FleetPulse.tenant.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.FleetPulse.tenant.domain.TenantStatus;
import com.FleetPulse.tenant.domain.TenantRole;

public final class TenantRequests {
	private TenantRequests() { }
	public record CreateTenantRequest(@NotBlank @Size(max = 200) String name) { }
	public record UpdateTenantStatusRequest(@NotNull TenantStatus status) { }
	public record AddTenantMemberRequest(@NotNull java.util.UUID userId, @NotNull TenantRole role) { }
}
