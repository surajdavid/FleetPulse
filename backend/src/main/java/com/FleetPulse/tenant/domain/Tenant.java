package com.FleetPulse.tenant.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "tenant")
public class Tenant {
	@Id
	private UUID id;
	@Column(nullable = false, length = 200)
	private String name;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 16)
	private TenantStatus status;
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	protected Tenant() { }

	public Tenant(String name) {
		this.id = UUID.randomUUID();
		this.name = name;
		this.status = TenantStatus.ACTIVE;
	}

	public void setStatus(TenantStatus status) { this.status = status; }

	@PrePersist
	void onCreate() { Instant now = Instant.now(); createdAt = now; updatedAt = now; }
	@PreUpdate
	void onUpdate() { updatedAt = Instant.now(); }

	public UUID getId() { return id; }
	public String getName() { return name; }
	public TenantStatus getStatus() { return status; }
	public Instant getCreatedAt() { return createdAt; }
	public Instant getUpdatedAt() { return updatedAt; }
}
