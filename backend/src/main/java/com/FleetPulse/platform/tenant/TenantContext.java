package com.FleetPulse.platform.tenant;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public final class TenantContext {
	private TenantContext() { }

	public static UUID requireTenantId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {
			throw new TenantContextMissingException();
		}
		String tenantId = jwtAuthentication.getToken().getClaimAsString("tenant_id");
		try {
			if (tenantId == null || tenantId.isBlank()) throw new IllegalArgumentException();
			return UUID.fromString(tenantId);
		} catch (IllegalArgumentException exception) {
			throw new TenantContextMissingException();
		}
	}

	public static boolean isPlatformAdministrator() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null && authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("SCOPE_platform:admin"));
	}

	public static UUID requireDriverId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) throw new TenantContextMissingException();
		String driverId = jwtAuthentication.getToken().getClaimAsString("driver_id");
		try {
			if (driverId == null || driverId.isBlank()) throw new IllegalArgumentException();
			return UUID.fromString(driverId);
		} catch (IllegalArgumentException exception) { throw new TenantContextMissingException(); }
	}
}
