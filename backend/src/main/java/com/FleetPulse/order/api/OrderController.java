package com.FleetPulse.order.api;

import java.net.URI;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import com.FleetPulse.order.application.OrderService;
import com.FleetPulse.order.api.OrderRequests.CreateOrderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
	private final OrderService orders;
	public OrderController(OrderService orders) { this.orders = orders; }

	@PostMapping
	public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request,
			@RequestHeader("Idempotency-Key") String idempotencyKey,
			HttpServletRequest servletRequest) {
		if (idempotencyKey.isBlank() || idempotencyKey.length() > 100) throw new IllegalArgumentException("Invalid idempotency key");
		OrderResponse response = OrderResponse.from(orders.create(request, idempotencyKey,
				String.valueOf(servletRequest.getAttribute("X-Correlation-ID"))));
		return ResponseEntity.created(URI.create("/api/v1/orders/" + response.id())).body(response);
	}

	@GetMapping("/{orderId}")
	public OrderResponse get(@PathVariable UUID orderId) { return OrderResponse.from(orders.get(orderId)); }
}
