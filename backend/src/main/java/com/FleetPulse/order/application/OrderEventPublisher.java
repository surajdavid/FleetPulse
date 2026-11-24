package com.FleetPulse.order.application;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.FleetPulse.order.domain.OutboxEvent;
import com.FleetPulse.order.infrastructure.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderEventPublisher {
	private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);
	private final OutboxEventRepository events;
	private final KafkaTemplate<String, String> kafka;

	public OrderEventPublisher(OutboxEventRepository events, KafkaTemplate<String, String> kafka) {
		this.events = events;
		this.kafka = kafka;
	}

	@Scheduled(fixedDelayString = "${fleetpulse.outbox.poll-delay-ms:1000}")
	@Transactional
	public void publishPending() {
		List<OutboxEvent> pending = events.lockNextBatch(Instant.now(), PageRequest.of(0, 50));
		for (OutboxEvent event : pending) {
			try {
				kafka.send(event.getTopic(), event.getAggregateId().toString(), event.getPayload()).get(10, TimeUnit.SECONDS);
				event.markPublished();
			} catch (Exception exception) {
				event.markFailed(exception.getClass().getSimpleName() + ": " + String.valueOf(exception.getMessage()));
				log.warn("Outbox event {} publish failed; retry scheduled", event.getEventId());
			}
		}
	}
}
