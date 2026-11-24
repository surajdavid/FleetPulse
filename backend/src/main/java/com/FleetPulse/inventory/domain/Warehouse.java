package com.FleetPulse.inventory.domain;

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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "warehouse", uniqueConstraints = @UniqueConstraint(name = "uk_warehouse_tenant_code", columnNames = {"tenant_id", "code"}))
public class Warehouse {
	@Id private UUID id;
	@Column(name = "tenant_id", nullable = false, updatable = false) private UUID tenantId;
	@Column(nullable = false, length = 64) private String code;
	@Column(nullable = false, length = 200) private String name;
	@Column(nullable = false, length = 500) private String address;
	@Enumerated(EnumType.STRING) @Column(nullable = false, length = 16) private WarehouseStatus status;
	@Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
	@Column(name = "updated_at", nullable = false) private Instant updatedAt;
	protected Warehouse() { }
	public Warehouse(UUID tenantId, String code, String name, String address) {
		this.id = UUID.randomUUID(); this.tenantId = tenantId; this.code = code; this.name = name;
		this.address = address; this.status = WarehouseStatus.ACTIVE;
	}
	@PrePersist void onCreate() { Instant now = Instant.now(); createdAt = now; updatedAt = now; }
	@PreUpdate void onUpdate() { updatedAt = Instant.now(); }
	public UUID getId() { return id; }
	public UUID getTenantId() { return tenantId; }
	public String getCode() { return code; }
	public String getName() { return name; }
	public String getAddress() { return address; }
	public WarehouseStatus getStatus() { return status; }
}
