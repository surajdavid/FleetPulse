package com.FleetPulse.delivery.application;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.FleetPulse.delivery.api.DeliveryRequests.CreateDriverRequest;
import com.FleetPulse.delivery.domain.AssignmentStatus;
import com.FleetPulse.delivery.domain.DeliveryAssignment;
import com.FleetPulse.delivery.domain.Driver;
import com.FleetPulse.delivery.domain.DriverStatus;
import com.FleetPulse.delivery.infrastructure.DeliveryAssignmentRepository;
import com.FleetPulse.delivery.infrastructure.DriverRepository;
import com.FleetPulse.order.domain.CustomerOrder;
import com.FleetPulse.order.domain.OrderStatus;
import com.FleetPulse.order.domain.OutboxEvent;
import com.FleetPulse.order.domain.Shipment;
import com.FleetPulse.order.infrastructure.OutboxEventRepository;
import com.FleetPulse.order.infrastructure.ShipmentRepository;
import com.FleetPulse.platform.tenant.TenantContext;
import com.FleetPulse.tenant.application.TenantAccessGuard;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeliveryService {
	private static final List<AssignmentStatus> ACTIVE_STATUSES = List.of(AssignmentStatus.ASSIGNED, AssignmentStatus.ACCEPTED,
			AssignmentStatus.PICKED_UP, AssignmentStatus.IN_TRANSIT, AssignmentStatus.OUT_FOR_DELIVERY, AssignmentStatus.RESCHEDULED);
	private final DriverRepository drivers;
	private final DeliveryAssignmentRepository assignments;
	private final ShipmentRepository shipments;
	private final TenantAccessGuard tenantAccess;
	private final OutboxEventRepository outbox;
	private final ObjectMapper objectMapper;

	public DeliveryService(DriverRepository drivers, DeliveryAssignmentRepository assignments, ShipmentRepository shipments,
			TenantAccessGuard tenantAccess, OutboxEventRepository outbox, ObjectMapper objectMapper) {
		this.drivers = drivers; this.assignments = assignments; this.shipments = shipments; this.tenantAccess = tenantAccess;
		this.outbox = outbox; this.objectMapper = objectMapper;
	}

	public Driver createDriver(CreateDriverRequest request) {
		UUID tenantId = requireActiveTenant();
		return drivers.save(new Driver(tenantId, request.displayName(), request.contactReference()));
	}

	@Transactional(readOnly = true)
	public List<Driver> availableDrivers() {
		return drivers.findAllByTenantIdAndStatus(requireActiveTenant(), DriverStatus.AVAILABLE);
	}

	public DeliveryAssignment assign(UUID shipmentId, UUID driverId, String key, String correlationId) {
		UUID tenantId = requireActiveTenant();
		var prior = assignments.findByTenantIdAndIdempotencyKey(tenantId, key);
		if (prior.isPresent()) {
			if (!prior.get().getShipmentId().equals(shipmentId) || !prior.get().getDriverId().equals(driverId)) throw new AssignmentConflictException();
			return prior.get();
		}
		Shipment shipment = shipments.findByTenantIdAndId(tenantId, shipmentId).orElseThrow(DeliveryNotFoundException::new);
		CustomerOrder order = shipment.getOrder();
		if (order.getStatus() != OrderStatus.CONFIRMED && order.getStatus() != OrderStatus.READY_FOR_DISPATCH) throw new AssignmentConflictException();
		if (assignments.hasActiveAssignment(tenantId, shipmentId, ACTIVE_STATUSES)) throw new AssignmentConflictException();
		Driver driver = drivers.findByTenantIdAndId(tenantId, driverId).orElseThrow(DeliveryNotFoundException::new);
		driver.markBusy();
		DeliveryAssignment assignment = assignments.save(new DeliveryAssignment(tenantId, shipmentId, driverId, key));
		publish("DeliveryAssigned", "fleetpulse.delivery.delivery-assigned.v1", tenantId, assignment.getId(), correlationId,
				new AssignmentEventPayload(assignment.getId(), shipmentId, driverId));
		return assignment;
	}

	public DeliveryAssignment update(UUID assignmentId, AssignmentStatus status, String reason) {
		UUID tenantId = requireActiveTenant();
		DeliveryAssignment assignment = assignments.findByTenantIdAndId(tenantId, assignmentId).orElseThrow(DeliveryNotFoundException::new);
		assignment.transitionTo(status, reason == null ? "" : reason);
		if (status == AssignmentStatus.ASSIGNED) {
			drivers.findByTenantIdAndId(tenantId, assignment.getDriverId()).orElseThrow(DeliveryNotFoundException::new).markBusy();
		} else if (status == AssignmentStatus.REJECTED || status == AssignmentStatus.FAILED || status == AssignmentStatus.DELIVERED || status == AssignmentStatus.RETURNED || status == AssignmentStatus.RESCHEDULED) {
			drivers.findByTenantIdAndId(tenantId, assignment.getDriverId()).orElseThrow(DeliveryNotFoundException::new).markAvailable();
		}
		return assignment;
	}

	public DeliveryAssignment submitProof(UUID assignmentId, String objectReference) {
		if (!objectReference.startsWith("s3://")) throw new IllegalArgumentException("Proof of delivery must be an S3 object reference");
		UUID tenantId = requireActiveTenant();
		DeliveryAssignment assignment = assignments.findByTenantIdAndId(tenantId, assignmentId).orElseThrow(DeliveryNotFoundException::new);
		assignment.submitProof(objectReference);
		return assignment;
	}

	public DeliveryAssignment submitProofForShipment(UUID shipmentId, String objectReference) {
		UUID tenantId = requireActiveTenant();
		List<DeliveryAssignment> current = assignments.findByShipmentAndStatuses(tenantId, shipmentId,
				List.of(AssignmentStatus.OUT_FOR_DELIVERY, AssignmentStatus.DELIVERED));
		if (current.isEmpty()) throw new DeliveryNotFoundException();
		return submitProof(current.getFirst().getId(), objectReference);
	}

	private UUID requireActiveTenant() {
		UUID tenantId = TenantContext.requireTenantId(); tenantAccess.requireActive(tenantId); return tenantId;
	}

	private void publish(String type, String topic, UUID tenantId, UUID aggregateId, String correlationId, Object payload) {
		try {
			AssignmentEvent event = new AssignmentEvent(UUID.randomUUID(), type, 1, Instant.now(), "delivery-service", tenantId,
					"DeliveryAssignment", aggregateId, correlationId, payload);
			outbox.save(new OutboxEvent(type, topic, aggregateId, objectMapper.writeValueAsString(event)));
		} catch (JsonProcessingException exception) { throw new IllegalStateException("Could not serialize delivery event", exception); }
	}
	public record AssignmentEventPayload(UUID assignmentId, UUID shipmentId, UUID driverId) { }
	public record AssignmentEvent(UUID eventId, String eventType, int eventVersion, Instant occurredAt, String producer,
			UUID tenantId, String aggregateType, UUID aggregateId, String correlationId, Object payload) { }
}
