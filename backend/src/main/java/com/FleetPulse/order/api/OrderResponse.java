package com.FleetPulse.order.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.FleetPulse.order.domain.CustomerOrder;
import com.FleetPulse.order.domain.OrderItem;
import com.FleetPulse.order.domain.OrderStatus;
import com.FleetPulse.order.domain.Shipment;
import com.FleetPulse.order.domain.ShipmentStatus;

public record OrderResponse(UUID id, String orderNumber, UUID customerId, OrderStatus status, String currency,
		BigDecimal subtotal, BigDecimal tax, BigDecimal total, List<OrderItemResponse> items,
		List<ShipmentResponse> shipments, Instant createdAt) {
	public static OrderResponse from(CustomerOrder order) {
		return new OrderResponse(order.getId(), order.getOrderNumber(), order.getCustomerId(), order.getStatus(),
				order.getCurrency(), order.getSubtotal(), order.getTax(), order.getTotal(),
				order.getItems().stream().map(OrderItemResponse::from).toList(),
				order.getShipments().stream().map(ShipmentResponse::from).toList(), order.getCreatedAt());
	}
	public record OrderItemResponse(String sku, String description, int quantity, BigDecimal unitPrice, BigDecimal lineTotal) {
		static OrderItemResponse from(OrderItem item) {
			return new OrderItemResponse(item.getSku(), item.getDescription(), item.getQuantity(), item.getUnitPrice(), item.getLineTotal());
		}
	}
	public record ShipmentResponse(UUID id, String shipmentNumber, UUID originWarehouseId,
			String destinationAddressSnapshot, ShipmentStatus status, Instant createdAt) {
		static ShipmentResponse from(Shipment shipment) {
			return new ShipmentResponse(shipment.getId(), shipment.getShipmentNumber(), shipment.getOriginWarehouseId(),
					shipment.getDestinationAddressSnapshot(), shipment.getStatus(), shipment.getCreatedAt());
		}
	}
}
