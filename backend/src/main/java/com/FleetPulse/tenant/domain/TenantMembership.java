package com.FleetPulse.tenant.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tenant_membership")
public class TenantMembership {
	@Id
	private UUID id;
	@Column(name = "tenant_id", nullable = false, updatable = false)
	private UUID tenantId;
	@Column(name = "user_id", nullable = false, updatable = false)
	private UUID userId;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 24)
	private TenantRole role;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 16)
	private MembershipStatus status;
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	protected TenantMembership() { }
	public TenantMembership(UUID tenantId, UUID userId, TenantRole role) {
		this.id = UUID.randomUUID();
		this.tenantId = tenantId;
		this.userId = userId;
		this.role = role;
		this.status = MembershipStatus.ACTIVE;
		this.createdAt = Instant.now();
	}
	public UUID getId() { return id; }
	public UUID getTenantId() { return tenantId; }
	public UUID getUserId() { return userId; }
	public TenantRole getRole() { return role; }
	public MembershipStatus getStatus() { return status; }
	public Instant getCreatedAt() { return createdAt; }
}
