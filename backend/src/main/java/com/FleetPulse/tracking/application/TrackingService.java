package com.FleetPulse.tracking.application;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.FleetPulse.delivery.domain.AssignmentStatus;
import com.FleetPulse.delivery.infrastructure.DeliveryAssignmentRepository;
import com.FleetPulse.order.domain.OutboxEvent;
import com.FleetPulse.order.infrastructure.OutboxEventRepository;
import com.FleetPulse.platform.tenant.TenantContext;
import com.FleetPulse.tenant.application.TenantAccessGuard;
import com.FleetPulse.tracking.api.LocationResponse;
import com.FleetPulse.tracking.api.LocationUpdateRequest;
import com.FleetPulse.tracking.domain.LocationUpdate;
import com.FleetPulse.tracking.domain.TrackingSession;
import com.FleetPulse.tracking.domain.TrackingStatus;
import com.FleetPulse.tracking.infrastructure.LocationUpdateRepository;
import com.FleetPulse.tracking.infrastructure.TrackingSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TrackingService {
	private static final Logger log = LoggerFactory.getLogger(TrackingService.class);
	private static final List<AssignmentStatus> TRACKABLE = List.of(AssignmentStatus.ACCEPTED, AssignmentStatus.PICKED_UP,
			AssignmentStatus.IN_TRANSIT, AssignmentStatus.OUT_FOR_DELIVERY);
	private final DeliveryAssignmentRepository assignments;
	private final TrackingSessionRepository sessions;
	private final LocationUpdateRepository locations;
	private final StringRedisTemplate redis;
	private final SimpMessagingTemplate websocket;
	private final OutboxEventRepository outbox;
	private final TenantAccessGuard tenantAccess;
	private final ObjectMapper objectMapper;

	public TrackingService(DeliveryAssignmentRepository assignments, TrackingSessionRepository sessions,
			LocationUpdateRepository locations, StringRedisTemplate redis, SimpMessagingTemplate websocket,
			OutboxEventRepository outbox, TenantAccessGuard tenantAccess, ObjectMapper objectMapper) {
		this.assignments = assignments; this.sessions = sessions; this.locations = locations; this.redis = redis;
		this.websocket = websocket; this.outbox = outbox; this.tenantAccess = tenantAccess; this.objectMapper = objectMapper;
	}

	public LocationResponse ingest(LocationUpdateRequest request, String correlationId) {
		UUID tenantId = TenantContext.requireTenantId();
		UUID driverId = TenantContext.requireDriverId();
		tenantAccess.requireActive(tenantId);
		if (!assignments.existsByTenantIdAndShipmentIdAndDriverIdAndStatusIn(tenantId, request.shipmentId(), driverId, TRACKABLE)) {
			throw new TrackingAccessDeniedException();
		}
		Instant now = Instant.now();
		if (request.recordedAt().isAfter(now.plusSeconds(120)) || request.recordedAt().isBefore(now.minus(Duration.ofHours(1)))) {
			throw new StaleLocationException();
		}
		TrackingSession session = sessions.findFirstByTenantIdAndShipmentIdAndStatusOrderByStartedAtDesc(tenantId,
				request.shipmentId(), TrackingStatus.ACTIVE).orElseGet(() -> sessions.save(new TrackingSession(tenantId, request.shipmentId(), driverId)));
		var latest = locations.findFirstByTenantIdAndTrackingSessionIdOrderByRecordedAtDesc(tenantId, session.getId());
		if (latest.isPresent() && !request.recordedAt().isAfter(latest.get().getRecordedAt())) throw new StaleLocationException();
		LocationUpdate saved = locations.save(new LocationUpdate(tenantId, session.getId(), request.latitude(), request.longitude(),
				request.accuracyMeters(), request.recordedAt()));
		LocationResponse response = LocationResponse.from(request.shipmentId(), saved);
		String cacheKey = "fleetpulse:" + tenantId + ":tracking:shipment:" + request.shipmentId() + ":latest";
		try { redis.opsForValue().set(cacheKey, objectMapper.writeValueAsString(response), Duration.ofHours(2)); }
		catch (Exception exception) { log.warn("Latest-location cache update failed for shipment {}", request.shipmentId()); }
		try { websocket.convertAndSend(topic(tenantId, request.shipmentId()), response); }
		catch (Exception exception) { log.warn("WebSocket location publish failed for shipment {}", request.shipmentId()); }
		try {
			LocationEvent event = new LocationEvent(UUID.randomUUID(), "LocationUpdated", 1, now, "tracking-service", tenantId,
					"TrackingSession", session.getId(), correlationId,
					new LocationEventPayload(request.shipmentId(), request.latitude(), request.longitude(), request.recordedAt()));
			outbox.save(new OutboxEvent("LocationUpdated", "fleetpulse.tracking.location-updated.v1", session.getId(),
					objectMapper.writeValueAsString(event)));
		} catch (JsonProcessingException exception) { throw new IllegalStateException("Could not serialize location event", exception); }
		return response;
	}

	@Transactional(readOnly = true)
	public List<LocationResponse> history(UUID shipmentId) {
		UUID tenantId = TenantContext.requireTenantId(); tenantAccess.requireActive(tenantId);
		TrackingSession session = sessions.findFirstByTenantIdAndShipmentIdAndStatusOrderByStartedAtDesc(tenantId,
				shipmentId, TrackingStatus.ACTIVE).orElseThrow(TrackingSessionNotFoundException::new);
		return locations.findByTenantIdAndTrackingSessionIdOrderByRecordedAtDesc(tenantId, session.getId(), PageRequest.of(0, 100))
				.stream().map(update -> LocationResponse.from(shipmentId, update)).toList();
	}

	private String topic(UUID tenantId, UUID shipmentId) { return "/topic/tenants/" + tenantId + "/shipments/" + shipmentId; }
	public record LocationEventPayload(UUID shipmentId, double latitude, double longitude, Instant recordedAt) { }
	public record LocationEvent(UUID eventId, String eventType, int eventVersion, Instant occurredAt, String producer,
			UUID tenantId, String aggregateType, UUID aggregateId, String correlationId, LocationEventPayload payload) { }
}
