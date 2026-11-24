package com.FleetPulse.inventory.application;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.FleetPulse.inventory.api.InventoryRequests.ReservationLineRequest;
import com.FleetPulse.inventory.api.InventoryRequests.ReserveInventoryRequest;
import com.FleetPulse.inventory.domain.InsufficientInventoryException;
import com.FleetPulse.order.application.OrderService.OrderLine;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedInventoryConsumer {
	private final InventoryService inventory;
	private final ObjectMapper objectMapper;

	public OrderCreatedInventoryConsumer(InventoryService inventory, ObjectMapper objectMapper) {
		this.inventory = inventory;
		this.objectMapper = objectMapper;
	}

	@KafkaListener(topics = "fleetpulse.order.order-created.v1", groupId = "inventory-service")
	public void onOrderCreated(String message) throws Exception {
		JsonNode event = objectMapper.readTree(message);
		UUID tenantId = UUID.fromString(event.path("tenantId").asText());
		String eventId = event.path("eventId").asText();
		String correlationId = event.path("correlationId").asText();
		JsonNode payload = event.path("payload");
		UUID shipmentId = UUID.fromString(payload.path("shipmentId").asText());
		JsonNode warehouseNode = payload.path("warehouseId");
		UUID warehouseId = warehouseNode.isNull() || warehouseNode.isMissingNode() ? null : UUID.fromString(warehouseNode.asText());
		List<ReservationLineRequest> lines = objectMapper.convertValue(payload.path("items"),
				objectMapper.getTypeFactory().constructCollectionType(List.class, ReservationLineRequest.class));
		try {
			if (warehouseId == null) throw new WarehouseNotFoundException();
			inventory.reserveForOrderEvent(tenantId, new ReserveInventoryRequest(shipmentId, warehouseId, lines), eventId, correlationId);
		} catch (InsufficientInventoryException | WarehouseNotFoundException exception) {
			String failedSku = exception instanceof InsufficientInventoryException ? exception.getMessage() : "WAREHOUSE_NOT_FOUND";
			inventory.publishRejected(tenantId, shipmentId, correlationId, failedSku);
		}
	}
}
