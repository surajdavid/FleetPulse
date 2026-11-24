package com.FleetPulse.payment.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.FleetPulse.payment.domain.PaymentStatus;
import com.FleetPulse.payment.domain.PaymentTransaction;

public record PaymentResponse(UUID id, UUID orderId, BigDecimal amount, String currency, PaymentStatus status,
		String transactionReference, boolean simulated, Instant createdAt) {
	public static PaymentResponse from(PaymentTransaction payment) {
		return new PaymentResponse(payment.getId(), payment.getOrderId(), payment.getAmount(), payment.getCurrency(),
				payment.getStatus(), payment.getTransactionReference(), payment.isSimulated(), payment.getCreatedAt());
	}
}
