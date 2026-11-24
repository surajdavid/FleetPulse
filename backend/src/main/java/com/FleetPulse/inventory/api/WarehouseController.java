package com.FleetPulse.inventory.api;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import com.FleetPulse.inventory.api.InventoryRequests.CreateWarehouseRequest;
import com.FleetPulse.inventory.api.InventoryRequests.StockAdjustmentRequest;
import com.FleetPulse.inventory.api.InventoryResponses.InventoryItemResponse;
import com.FleetPulse.inventory.api.InventoryResponses.WarehouseResponse;
import com.FleetPulse.inventory.application.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/warehouses")
public class WarehouseController {
	private final InventoryService inventory;
	public WarehouseController(InventoryService inventory) { this.inventory = inventory; }
	@PostMapping
	public ResponseEntity<WarehouseResponse> create(@Valid @RequestBody CreateWarehouseRequest request) {
		WarehouseResponse response = WarehouseResponse.from(inventory.createWarehouse(request));
		return ResponseEntity.created(URI.create("/api/v1/warehouses/" + response.id())).body(response);
	}
	@GetMapping("/{warehouseId}/inventory")
	public List<InventoryItemResponse> getInventory(@PathVariable UUID warehouseId) {
		return inventory.getInventory(warehouseId).stream().map(InventoryItemResponse::from).toList();
	}
	@PostMapping("/{warehouseId}/stock-adjustments")
	public InventoryItemResponse adjustStock(@PathVariable UUID warehouseId, @Valid @RequestBody StockAdjustmentRequest request) {
		return InventoryItemResponse.from(inventory.adjustStock(warehouseId, request));
	}
}
