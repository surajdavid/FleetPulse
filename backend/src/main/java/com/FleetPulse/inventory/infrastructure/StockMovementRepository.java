package com.FleetPulse.inventory.infrastructure;

import java.util.UUID;

import com.FleetPulse.inventory.domain.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> { }
