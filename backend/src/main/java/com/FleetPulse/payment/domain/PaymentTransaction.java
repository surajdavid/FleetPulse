package com.FleetPulse.payment.domain;

import java.math.BigDecimal;
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
@Table(name = "payment_transaction", uniqueConstraints = {
		@UniqueConstraint(name = "uk_payment_tenant_idempotency", columnNames = {"tenant_id", "idempotency_key"}),
		@UniqueConstraint(name = "uk_payment_transaction_reference", columnNames = "transaction_reference")})
public class PaymentTransaction {
	@Id private UUID id;
	@Column(name = "tenant_id", nullable = false, updatable = false) private UUID tenantId;
	@Column(name = "order_id", nullable = false, updatable = false) private UUID orderId;
	@Column(nullable = false, precision = 19, scale = 4, updatable = false) private BigDecimal amount;
	@Column(nullable = false, length = 3, updatable = false) private String currency;
	@Enumerated(EnumType.STRING) @Column(nullable = false, length = 24) private PaymentStatus status;
	@Column(name = "transaction_reference", nullable = false, length = 40, updatable = false) private String transactionReference;
	@Column(name = "provider_reference", length = 80, updatable = false) private String providerReference;
	@Column(nullable = false, updatable = false) private boolean simulated;
	@Column(name = "idempotency_key", nullable = false, length = 100, updatable = false) private String idempotencyKey;
	@Column(name = "request_hash", nullable = false, length = 64, updatable = false) private String requestHash;
	@Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
	@Column(name = "updated_at", nullable = false) private Instant updatedAt;
	protected PaymentTransaction() { }
	public PaymentTransaction(UUID tenantId, UUID orderId, BigDecimal amount, String currency, PaymentStatus status,
			String key, String hash) {
		this.id = UUID.randomUUID(); this.tenantId = tenantId; this.orderId = orderId; this.amount = amount;
		this.currency = currency; this.status = status; this.simulated = true; this.idempotencyKey = key;
		this.requestHash = hash; this.transactionReference = "SIM-" + UUID.randomUUID().toString();
		this.providerReference = this.transactionReference;
	}
	public boolean matchesRequest(String hash) { return requestHash.equals(hash); }
	@PrePersist void onCreate() { Instant now = Instant.now(); createdAt = now; updatedAt = now; }
	@PreUpdate void onUpdate() { updatedAt = Instant.now(); }
	public UUID getId() { return id; }
	public UUID getOrderId() { return orderId; }
	public BigDecimal getAmount() { return amount; }
	public String getCurrency() { return currency; }
	public PaymentStatus getStatus() { return status; }
	public String getTransactionReference() { return transactionReference; }
	public boolean isSimulated() { return simulated; }
	public Instant getCreatedAt() { return createdAt; }
}
