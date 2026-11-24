package com.FleetPulse.order.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "outbox_event")
public class OutboxEvent {
	@Id private UUID eventId;
	@Column(name = "event_type", nullable = false, length = 100) private String eventType;
	@Column(nullable = false, length = 100) private String topic;
	@Column(name = "aggregate_id", nullable = false) private UUID aggregateId;
	@Column(nullable = false, columnDefinition = "TEXT") private String payload;
	@Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
	@Column(name = "next_attempt_at", nullable = false) private Instant nextAttemptAt;
	@Column(name = "published_at") private Instant publishedAt;
	@Column(nullable = false) private int attempts;
	@Column(name = "last_error", length = 500) private String lastError;

	protected OutboxEvent() { }
	public OutboxEvent(String eventType, String topic, UUID aggregateId, String payload) {
		this.eventId = UUID.randomUUID(); this.eventType = eventType; this.topic = topic;
		this.aggregateId = aggregateId; this.payload = payload; this.nextAttemptAt = Instant.now();
	}
	@PrePersist void onCreate() { createdAt = Instant.now(); }
	public void markPublished() { publishedAt = Instant.now(); lastError = null; }
	public void markFailed(String error) {
		attempts++;
		long delaySeconds = Math.min(300, 1L << Math.min(attempts, 8));
		nextAttemptAt = Instant.now().plusSeconds(delaySeconds);
		lastError = error.length() > 500 ? error.substring(0, 500) : error;
	}
	public UUID getEventId() { return eventId; }
	public String getTopic() { return topic; }
	public UUID getAggregateId() { return aggregateId; }
	public String getPayload() { return payload; }
}
