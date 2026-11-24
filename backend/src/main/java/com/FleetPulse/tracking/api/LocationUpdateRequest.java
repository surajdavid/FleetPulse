package com.FleetPulse.tracking.api;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record LocationUpdateRequest(@NotNull UUID shipmentId,
		@NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
		@NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
		@DecimalMin("0.0") @DecimalMax("100000.0") Double accuracyMeters,
		@NotNull Instant recordedAt) { }
