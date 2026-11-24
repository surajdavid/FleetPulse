package com.FleetPulse.payment.api;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import com.FleetPulse.payment.domain.PaymentOutcome;

public final class PaymentRequests {
	private PaymentRequests() { }
	public record SimulatePaymentRequest(@NotNull UUID orderId, @NotNull @DecimalMin("0.0001") @Digits(integer = 15, fraction = 4) BigDecimal amount,
			@NotNull @Pattern(regexp = "[A-Z]{3}") String currency, @NotNull PaymentOutcome outcome) { }
}
