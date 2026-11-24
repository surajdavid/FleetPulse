package com.FleetPulse.inventory.api;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public final class InventoryRequests {
	private InventoryRequests() { }
	public record CreateWarehouseRequest(@NotBlank @Size(max = 64) @Pattern(regexp = "[A-Za-z0-9._-]+") String code,
			@NotBlank @Size(max = 200) String name, @NotBlank @Size(max = 500) String address) { }
	public record StockAdjustmentRequest(@NotBlank @Size(max = 100) String sku, @Positive long quantity, @NotNull StockAdjustmentType type) { }
	public enum StockAdjustmentType { RECEIVE, ISSUE }
	public record ReserveInventoryRequest(@NotNull UUID shipmentId, @NotNull UUID warehouseId,
			@NotEmpty @Size(max = 100) List<@Valid ReservationLineRequest> items) { }
	public record ReservationLineRequest(@NotBlank @Size(max = 100) String sku, @Positive long quantity) { }
}
