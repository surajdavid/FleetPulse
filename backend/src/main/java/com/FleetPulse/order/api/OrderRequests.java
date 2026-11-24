package com.FleetPulse.order.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public final class OrderRequests {
	private OrderRequests() { }
	public record CreateOrderRequest(@NotNull UUID customerId, @NotNull UUID destinationAddressId, UUID originWarehouseId,
			@NotBlank @Pattern(regexp = "[A-Z]{3}") String currency,
			@NotNull @DecimalMin("0.0000") BigDecimal tax,
			@NotEmpty @Size(max = 100) List<@Valid OrderItemRequest> items) { }
	public record OrderItemRequest(@NotBlank @Size(max = 100) String sku, @NotBlank @Size(max = 300) String description,
			@Positive int quantity, @NotNull @DecimalMin("0.0000") @Digits(integer = 15, fraction = 4) BigDecimal unitPrice) { }
}
