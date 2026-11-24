package com.FleetPulse.order.infrastructure;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.FleetPulse.order.domain.OutboxEvent;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select event from OutboxEvent event where event.publishedAt is null and event.nextAttemptAt <= :now order by event.createdAt")
	List<OutboxEvent> lockNextBatch(@Param("now") Instant now, org.springframework.data.domain.Pageable pageable);
}
