package com.FleetPulse.platform.api;

import java.util.List;

public record ApiProblem(String type, String title, int status, String detail, String instance,
		String code, String correlationId, List<FieldError> fieldErrors) {
	public record FieldError(String field, String message) { }
}
