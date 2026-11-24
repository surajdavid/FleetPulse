package com.FleetPulse.payment.infrastructure;

import java.util.Optional;
import java.util.UUID;

import com.FleetPulse.payment.domain.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentTransaction, UUID> {
	Optional<PaymentTransaction> findByTenantIdAndId(UUID tenantId, UUID id);
	Optional<PaymentTransaction> findByTenantIdAndIdempotencyKey(UUID tenantId, String key);
}
