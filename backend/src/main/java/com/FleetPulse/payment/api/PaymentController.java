package com.FleetPulse.payment.api;

import java.net.URI;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import com.FleetPulse.payment.api.PaymentRequests.SimulatePaymentRequest;
import com.FleetPulse.payment.application.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
	private final PaymentService payments;
	public PaymentController(PaymentService payments) { this.payments = payments; }
	@PostMapping
	public ResponseEntity<PaymentResponse> simulate(@Valid @RequestBody SimulatePaymentRequest request,
			@RequestHeader("Idempotency-Key") String key, HttpServletRequest servletRequest) {
		if (key.isBlank() || key.length() > 100) throw new IllegalArgumentException("Invalid idempotency key");
		String correlationId = String.valueOf(servletRequest.getAttribute("X-Correlation-ID"));
		PaymentResponse response = PaymentResponse.from(payments.simulate(request, key, correlationId));
		return ResponseEntity.created(URI.create("/api/v1/payments/" + response.id())).body(response);
	}
	@GetMapping("/{paymentId}")
	public PaymentResponse get(@PathVariable UUID paymentId) { return PaymentResponse.from(payments.get(paymentId)); }
}
