package com.FleetPulse.customer.domain;

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
@Table(name = "customer", uniqueConstraints = @UniqueConstraint(name = "uk_customer_tenant_code", columnNames = {"tenant_id", "customer_code"}))
public class Customer {
	@Id
	private UUID id;
	@Column(name = "tenant_id", nullable = false, updatable = false)
	private UUID tenantId;
	@Column(name = "customer_code", nullable = false, length = 64)
	private String customerCode;
	@Column(nullable = false, length = 200)
	private String name;
	@Column(length = 320)
	private String email;
	@Column(length = 32)
	private String phone;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 16)
	private CustomerStatus status;
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	protected Customer() { }

	public Customer(UUID tenantId, String customerCode, String name, String email, String phone) {
		this.id = UUID.randomUUID();
		this.tenantId = tenantId;
		this.customerCode = customerCode;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.status = CustomerStatus.ACTIVE;
	}

	public void update(String name, String email, String phone) {
		if (name != null) this.name = name;
		if (email != null) this.email = email;
		if (phone != null) this.phone = phone;
	}

	@PrePersist
	void onCreate() { Instant now = Instant.now(); createdAt = now; updatedAt = now; }
	@PreUpdate
	void onUpdate() { updatedAt = Instant.now(); }

	public UUID getId() { return id; }
	public UUID getTenantId() { return tenantId; }
	public String getCustomerCode() { return customerCode; }
	public String getName() { return name; }
	public String getEmail() { return email; }
	public String getPhone() { return phone; }
	public CustomerStatus getStatus() { return status; }
	public Instant getCreatedAt() { return createdAt; }
}
