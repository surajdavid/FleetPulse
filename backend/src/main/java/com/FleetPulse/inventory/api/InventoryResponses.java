package com.FleetPulse.inventory.api;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.FleetPulse.inventory.domain.InventoryItem;
import com.FleetPulse.inventory.domain.InventoryReservation;
import com.FleetPulse.inventory.domain.ReservationStatus;
import com.FleetPulse.inventory.domain.Warehouse;
import com.FleetPulse.inventory.domain.WarehouseStatus;

public final class InventoryResponses {
	private InventoryResponses() { }
	public record WarehouseResponse(UUID id, String code, String name, String address, WarehouseStatus status) {
		public static WarehouseResponse from(Warehouse warehouse) {
			return new WarehouseResponse(warehouse.getId(), warehouse.getCode(), warehouse.getName(), warehouse.getAddress(), warehouse.getStatus());
		}
	}
	public record InventoryItemResponse(String sku, long quantityOnHand, long quantityReserved, long available) {
		public static InventoryItemResponse from(InventoryItem item) {
			return new InventoryItemResponse(item.getSku(), item.getQuantityOnHand(), item.getQuantityReserved(), item.getAvailable());
		}
	}
	public record ReservationResponse(UUID id, String sku, long quantity, ReservationStatus status, Instant createdAt) {
		public static ReservationResponse from(InventoryReservation reservation) {
			return new ReservationResponse(reservation.getId(), reservation.getSku(), reservation.getQuantity(), reservation.getStatus(), reservation.getCreatedAt());
		}
	}
}
