package com.FleetPulse.tenant.api;

import java.net.URI;
import java.util.UUID;

import jakarta.validation.Valid;
import com.FleetPulse.tenant.api.TenantRequests.CreateTenantRequest;
import com.FleetPulse.tenant.api.TenantRequests.UpdateTenantStatusRequest;
import com.FleetPulse.tenant.api.TenantRequests.AddTenantMemberRequest;
import com.FleetPulse.tenant.application.TenantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tenants")
public class TenantController {
	private final TenantService tenants;

	public TenantController(TenantService tenants) { this.tenants = tenants; }

	@PostMapping
	public ResponseEntity<TenantResponse> create(@Valid @RequestBody CreateTenantRequest request) {
		TenantResponse response = TenantResponse.from(tenants.create(request));
		return ResponseEntity.created(URI.create("/api/v1/tenants/" + response.id())).body(response);
	}

	@GetMapping("/{tenantId}")
	public TenantResponse get(@PathVariable UUID tenantId) { return TenantResponse.from(tenants.get(tenantId)); }

	@PatchMapping("/{tenantId}/status")
	public TenantResponse updateStatus(@PathVariable UUID tenantId, @Valid @RequestBody UpdateTenantStatusRequest request) {
		return TenantResponse.from(tenants.updateStatus(tenantId, request));
	}

	@PostMapping("/{tenantId}/members")
	public ResponseEntity<TenantResponse.MemberResponse> addMember(@PathVariable UUID tenantId,
			@Valid @RequestBody AddTenantMemberRequest request) {
		TenantResponse.MemberResponse response = TenantResponse.MemberResponse.from(
				tenants.addMember(tenantId, request.userId(), request.role()));
		return ResponseEntity.created(URI.create("/api/v1/tenants/" + tenantId + "/members/" + response.id())).body(response);
	}
}
