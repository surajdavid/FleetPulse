package com.FleetPulse.delivery.domain;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name = "driver")
public class Driver {
	@Id private UUID id;
	@Column(name = "tenant_id", nullable = false, updatable = false) private UUID tenantId;
	@Column(name = "display_name", nullable = false, length = 200) private String displayName;
	@Column(name = "contact_reference", nullable = false, length = 200) private String contactReference;
	@Enumerated(EnumType.STRING) @Column(nullable = false, length = 16) private DriverStatus status;
	protected Driver() { }
	public Driver(UUID tenantId, String displayName, String contactReference) {
		this.id = UUID.randomUUID(); this.tenantId = tenantId; this.displayName = displayName;
		this.contactReference = contactReference; this.status = DriverStatus.AVAILABLE;
	}
	public void markBusy() { if (status != DriverStatus.AVAILABLE) throw new DriverUnavailableException(); status = DriverStatus.BUSY; }
	public void markAvailable() { if (status != DriverStatus.INACTIVE) status = DriverStatus.AVAILABLE; }
	public UUID getId() { return id; }
	public String getDisplayName() { return displayName; }
	public DriverStatus getStatus() { return status; }
}
