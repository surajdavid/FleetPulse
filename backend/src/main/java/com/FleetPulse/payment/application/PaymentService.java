package com.FleetPulse.payment.application;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.FleetPulse.order.domain.CustomerOrder;
import com.FleetPulse.order.domain.OutboxEvent;
import com.FleetPulse.order.infrastructure.CustomerOrderRepository;
import com.FleetPulse.order.infrastructure.OutboxEventRepository;
import com.FleetPulse.payment.api.PaymentRequests.SimulatePaymentRequest;
import com.FleetPulse.payment.domain.PaymentOutcome;
import com.FleetPulse.payment.domain.PaymentStatus;
import com.FleetPulse.payment.domain.PaymentTransaction;
import com.FleetPulse.payment.infrastructure.PaymentRepository;
import com.FleetPulse.platform.tenant.TenantContext;
import com.FleetPulse.tenant.application.TenantAccessGuard;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentService {
	private final PaymentRepository payments;
	private final CustomerOrderRepository orders;
	private final OutboxEventRepository outbox;
	private final TenantAccessGuard tenantAccess;
	private final ObjectMapper objectMapper;

	public PaymentService(PaymentRepository payments, CustomerOrderRepository orders, OutboxEventRepository outbox,
			TenantAccessGuard tenantAccess, ObjectMapper objectMapper) {
		this.payments = payments; this.orders = orders; this.outbox = outbox;
		this.tenantAccess = tenantAccess; this.objectMapper = objectMapper;
	}

	public PaymentTransaction simulate(SimulatePaymentRequest request, String key, String correlationId) {
		UUID tenantId = TenantContext.requireTenantId();
		tenantAccess.requireActive(tenantId);
		String hash = hash(request);
		var existing = payments.findByTenantIdAndIdempotencyKey(tenantId, key);
		if (existing.isPresent()) {
			if (!existing.get().matchesRequest(hash)) throw new PaymentConflictException();
			return existing.get();
		}
		CustomerOrder order = orders.findByTenantIdAndId(tenantId, request.orderId()).orElseThrow(PaymentConflictException::new);
		if (order.getStatus() != com.FleetPulse.order.domain.OrderStatus.PAYMENT_PENDING ||
				order.getTotal().compareTo(request.amount()) != 0 || !order.getCurrency().equals(request.currency())) {
			throw new PaymentConflictException();
		}
		PaymentStatus status = request.outcome() == PaymentOutcome.SUCCESS ? PaymentStatus.SUCCESSFUL : PaymentStatus.FAILED;
		PaymentTransaction payment = payments.save(new PaymentTransaction(tenantId, request.orderId(), request.amount(),
				request.currency(), status, key, hash));
		String eventType = status == PaymentStatus.SUCCESSFUL ? "PaymentCompleted" : "PaymentFailed";
		try {
			PaymentEvent event = new PaymentEvent(UUID.randomUUID(), eventType, 1, java.time.Instant.now(), "payment-service",
					tenantId, "Payment", payment.getId(), correlationId,
					new PaymentEventPayload(payment.getId(), payment.getOrderId(), payment.getAmount(), payment.getCurrency(), status, true));
			outbox.save(new OutboxEvent(eventType, "fleetpulse.payment." + kebab(eventType) + ".v1", payment.getId(),
					objectMapper.writeValueAsString(event)));
		} catch (JsonProcessingException exception) {
			throw new IllegalStateException("Could not serialize payment event", exception);
		}
		return payment;
	}

	@Transactional(readOnly = true)
	public PaymentTransaction get(UUID id) {
		UUID tenantId = TenantContext.requireTenantId(); tenantAccess.requireActive(tenantId);
		return payments.findByTenantIdAndId(tenantId, id).orElseThrow(PaymentNotFoundException::new);
	}

	private String hash(SimulatePaymentRequest request) {
		try {
			return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(request.toString().getBytes(StandardCharsets.UTF_8)));
		} catch (NoSuchAlgorithmException exception) { throw new IllegalStateException("SHA-256 is unavailable", exception); }
	}
	private String kebab(String value) { return value.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase(java.util.Locale.ROOT); }
	public record PaymentEventPayload(UUID paymentId, UUID orderId, BigDecimal amount, String currency, PaymentStatus status, boolean simulated) { }
	public record PaymentEvent(UUID eventId, String eventType, int eventVersion, java.time.Instant occurredAt, String producer,
			UUID tenantId, String aggregateType, UUID aggregateId, String correlationId, PaymentEventPayload payload) { }
}
