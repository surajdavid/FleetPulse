package com.FleetPulse.platform.api;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import com.FleetPulse.customer.application.CustomerNotFoundException;
import com.FleetPulse.customer.application.CustomerAddressNotFoundException;
import com.FleetPulse.platform.tenant.TenantContextMissingException;
import com.FleetPulse.tenant.application.TenantAccessDeniedException;
import com.FleetPulse.tenant.application.TenantNotFoundException;
import com.FleetPulse.tenant.application.TenantMembershipAlreadyExistsException;
import com.FleetPulse.order.application.OrderNotFoundException;
import com.FleetPulse.order.application.IdempotencyConflictException;
import com.FleetPulse.order.domain.InvalidOrderTransitionException;
import com.FleetPulse.order.domain.InvalidShipmentTransitionException;
import com.FleetPulse.order.application.ShipmentNotFoundException;
import com.FleetPulse.inventory.application.WarehouseNotFoundException;
import com.FleetPulse.inventory.application.WarehouseCodeConflictException;
import com.FleetPulse.inventory.application.ReservationConflictException;
import com.FleetPulse.inventory.domain.InsufficientInventoryException;
import com.FleetPulse.inventory.application.ReservationNotFoundException;
import com.FleetPulse.tenant.application.TenantSuspendedException;
import com.FleetPulse.payment.application.PaymentNotFoundException;
import com.FleetPulse.payment.application.PaymentConflictException;
import com.FleetPulse.delivery.application.DeliveryNotFoundException;
import com.FleetPulse.delivery.application.AssignmentConflictException;
import com.FleetPulse.delivery.domain.DriverUnavailableException;
import com.FleetPulse.delivery.domain.InvalidAssignmentTransitionException;
import com.FleetPulse.tracking.application.StaleLocationException;
import com.FleetPulse.tracking.application.TrackingAccessDeniedException;
import com.FleetPulse.tracking.application.TrackingSessionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {
	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ApiProblem> validation(MethodArgumentNotValidException exception, HttpServletRequest request) {
		List<ApiProblem.FieldError> fields = exception.getBindingResult().getFieldErrors().stream()
				.map(error -> new ApiProblem.FieldError(error.getField(), error.getDefaultMessage() == null ? "invalid value" : error.getDefaultMessage()))
				.toList();
		return problem(HttpStatus.BAD_REQUEST, "Validation failed", "One or more fields are invalid.", "VALIDATION_ERROR", fields, request);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	ResponseEntity<ApiProblem> invalidParameter(MethodArgumentTypeMismatchException exception, HttpServletRequest request) {
		return problem(HttpStatus.BAD_REQUEST, "Invalid parameter", "A request parameter has an invalid format.", "INVALID_PARAMETER", List.of(), request);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	ResponseEntity<ApiProblem> invalidRequest(IllegalArgumentException exception, HttpServletRequest request) {
		return problem(HttpStatus.BAD_REQUEST, "Invalid request", "A request parameter is outside the supported range.", "INVALID_REQUEST", List.of(), request);
	}

	@ExceptionHandler(TenantContextMissingException.class)
	ResponseEntity<ApiProblem> missingTenant(HttpServletRequest request) {
		return problem(HttpStatus.FORBIDDEN, "Tenant context required", "The authenticated identity is not associated with a tenant.", "TENANT_CONTEXT_REQUIRED", List.of(), request);
	}

	@ExceptionHandler(CustomerNotFoundException.class)
	ResponseEntity<ApiProblem> notFound(CustomerNotFoundException exception, HttpServletRequest request) {
		return problem(HttpStatus.NOT_FOUND, "Resource not found", "The requested customer was not found.", "CUSTOMER_NOT_FOUND", List.of(), request);
	}

	@ExceptionHandler(CustomerAddressNotFoundException.class)
	ResponseEntity<ApiProblem> addressNotFound(CustomerAddressNotFoundException exception, HttpServletRequest request) {
		return problem(HttpStatus.NOT_FOUND, "Resource not found", "The requested address was not found.", "ADDRESS_NOT_FOUND", List.of(), request);
	}

	@ExceptionHandler(TenantNotFoundException.class)
	ResponseEntity<ApiProblem> tenantNotFound(TenantNotFoundException exception, HttpServletRequest request) {
		return problem(HttpStatus.NOT_FOUND, "Resource not found", "The requested tenant was not found.", "TENANT_NOT_FOUND", List.of(), request);
	}

	@ExceptionHandler(TenantAccessDeniedException.class)
	ResponseEntity<ApiProblem> tenantAccessDenied(TenantAccessDeniedException exception, HttpServletRequest request) {
		return problem(HttpStatus.FORBIDDEN, "Access denied", "You are not authorized to access this tenant.", "ACCESS_DENIED", List.of(), request);
	}

	@ExceptionHandler(TenantMembershipAlreadyExistsException.class)
	ResponseEntity<ApiProblem> membershipConflict(TenantMembershipAlreadyExistsException exception, HttpServletRequest request) {
		return problem(HttpStatus.CONFLICT, "Membership already exists", "The user is already a member of this tenant.", "MEMBERSHIP_EXISTS", List.of(), request);
	}

	@ExceptionHandler(OrderNotFoundException.class)
	ResponseEntity<ApiProblem> orderNotFound(OrderNotFoundException exception, HttpServletRequest request) {
		return problem(HttpStatus.NOT_FOUND, "Resource not found", "The requested order was not found.", "ORDER_NOT_FOUND", List.of(), request);
	}

	@ExceptionHandler(IdempotencyConflictException.class)
	ResponseEntity<ApiProblem> idempotencyConflict(IdempotencyConflictException exception, HttpServletRequest request) {
		return problem(HttpStatus.CONFLICT, "Idempotency conflict", "The idempotency key was already used for a different request.", "IDEMPOTENCY_CONFLICT", List.of(), request);
	}

	@ExceptionHandler(InvalidOrderTransitionException.class)
	ResponseEntity<ApiProblem> invalidOrderTransition(InvalidOrderTransitionException exception, HttpServletRequest request) {
		return problem(HttpStatus.CONFLICT, "Invalid order transition", exception.getMessage(), "INVALID_ORDER_TRANSITION", List.of(), request);
	}

	@ExceptionHandler(InvalidShipmentTransitionException.class)
	ResponseEntity<ApiProblem> invalidShipmentTransition(InvalidShipmentTransitionException exception, HttpServletRequest request) {
		return problem(HttpStatus.CONFLICT, "Invalid shipment transition", exception.getMessage(), "INVALID_SHIPMENT_TRANSITION", List.of(), request);
	}

	@ExceptionHandler(ShipmentNotFoundException.class)
	ResponseEntity<ApiProblem> shipmentNotFound(ShipmentNotFoundException exception, HttpServletRequest request) {
		return problem(HttpStatus.NOT_FOUND, "Resource not found", "The requested shipment was not found.", "SHIPMENT_NOT_FOUND", List.of(), request);
	}

	@ExceptionHandler(WarehouseNotFoundException.class)
	ResponseEntity<ApiProblem> warehouseNotFound(WarehouseNotFoundException exception, HttpServletRequest request) {
		return problem(HttpStatus.NOT_FOUND, "Resource not found", "The requested warehouse was not found.", "WAREHOUSE_NOT_FOUND", List.of(), request);
	}

	@ExceptionHandler(WarehouseCodeConflictException.class)
	ResponseEntity<ApiProblem> warehouseConflict(WarehouseCodeConflictException exception, HttpServletRequest request) {
		return problem(HttpStatus.CONFLICT, "Warehouse code exists", "A warehouse with this code already exists in the tenant.", "WAREHOUSE_CODE_EXISTS", List.of(), request);
	}

	@ExceptionHandler(ReservationConflictException.class)
	ResponseEntity<ApiProblem> reservationConflict(ReservationConflictException exception, HttpServletRequest request) {
		return problem(HttpStatus.CONFLICT, "Idempotency conflict", "The idempotency key was already used for a different reservation.", "IDEMPOTENCY_CONFLICT", List.of(), request);
	}

	@ExceptionHandler(InsufficientInventoryException.class)
	ResponseEntity<ApiProblem> insufficientInventory(InsufficientInventoryException exception, HttpServletRequest request) {
		return problem(HttpStatus.CONFLICT, "Insufficient inventory", exception.getMessage(), "INSUFFICIENT_INVENTORY", List.of(), request);
	}

	@ExceptionHandler(ReservationNotFoundException.class)
	ResponseEntity<ApiProblem> reservationNotFound(ReservationNotFoundException exception, HttpServletRequest request) {
		return problem(HttpStatus.NOT_FOUND, "Resource not found", "The requested reservation was not found.", "RESERVATION_NOT_FOUND", List.of(), request);
	}

	@ExceptionHandler(TenantSuspendedException.class)
	ResponseEntity<ApiProblem> tenantSuspended(TenantSuspendedException exception, HttpServletRequest request) {
		return problem(HttpStatus.FORBIDDEN, "Tenant suspended", "Business operations are unavailable for this tenant.", "TENANT_SUSPENDED", List.of(), request);
	}

	@ExceptionHandler(PaymentNotFoundException.class)
	ResponseEntity<ApiProblem> paymentNotFound(PaymentNotFoundException exception, HttpServletRequest request) {
		return problem(HttpStatus.NOT_FOUND, "Resource not found", "The requested payment was not found.", "PAYMENT_NOT_FOUND", List.of(), request);
	}

	@ExceptionHandler(PaymentConflictException.class)
	ResponseEntity<ApiProblem> paymentConflict(PaymentConflictException exception, HttpServletRequest request) {
		return problem(HttpStatus.CONFLICT, "Payment conflict", "The order, payment amount, currency, or idempotency key conflicts with the request.", "PAYMENT_CONFLICT", List.of(), request);
	}

	@ExceptionHandler(DeliveryNotFoundException.class)
	ResponseEntity<ApiProblem> deliveryNotFound(DeliveryNotFoundException exception, HttpServletRequest request) {
		return problem(HttpStatus.NOT_FOUND, "Resource not found", "The requested delivery resource was not found.", "DELIVERY_NOT_FOUND", List.of(), request);
	}

	@ExceptionHandler({AssignmentConflictException.class, DriverUnavailableException.class})
	ResponseEntity<ApiProblem> assignmentConflict(RuntimeException exception, HttpServletRequest request) {
		return problem(HttpStatus.CONFLICT, "Assignment conflict", "The driver or shipment is not available for this assignment.", "ASSIGNMENT_CONFLICT", List.of(), request);
	}

	@ExceptionHandler(InvalidAssignmentTransitionException.class)
	ResponseEntity<ApiProblem> invalidAssignmentTransition(InvalidAssignmentTransitionException exception, HttpServletRequest request) {
		return problem(HttpStatus.CONFLICT, "Invalid assignment transition", exception.getMessage(), "INVALID_ASSIGNMENT_TRANSITION", List.of(), request);
	}

	@ExceptionHandler(StaleLocationException.class)
	ResponseEntity<ApiProblem> staleLocation(StaleLocationException exception, HttpServletRequest request) {
		return problem(HttpStatus.CONFLICT, "Stale location", "The GPS update is outside the accepted time window or older than the latest update.", "STALE_LOCATION", List.of(), request);
	}

	@ExceptionHandler(TrackingAccessDeniedException.class)
	ResponseEntity<ApiProblem> trackingDenied(TrackingAccessDeniedException exception, HttpServletRequest request) {
		return problem(HttpStatus.FORBIDDEN, "Tracking access denied", "The authenticated delivery partner is not assigned to this shipment.", "TRACKING_ACCESS_DENIED", List.of(), request);
	}

	@ExceptionHandler(TrackingSessionNotFoundException.class)
	ResponseEntity<ApiProblem> trackingNotFound(TrackingSessionNotFoundException exception, HttpServletRequest request) {
		return problem(HttpStatus.NOT_FOUND, "Tracking unavailable", "No active tracking session was found for this shipment.", "TRACKING_NOT_FOUND", List.of(), request);
	}

	private ResponseEntity<ApiProblem> problem(HttpStatus status, String title, String detail, String code,
			List<ApiProblem.FieldError> fields, HttpServletRequest request) {
		Object correlationId = request.getAttribute("X-Correlation-ID");
		ApiProblem body = new ApiProblem("about:blank", title, status.value(), detail, request.getRequestURI(), code,
				correlationId == null ? "unknown" : correlationId.toString(), fields);
		return ResponseEntity.status(status).body(body);
	}
}
