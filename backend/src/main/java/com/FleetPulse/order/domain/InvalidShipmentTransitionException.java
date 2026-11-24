package com.FleetPulse.order.domain;

public class InvalidShipmentTransitionException extends RuntimeException {
	public InvalidShipmentTransitionException(ShipmentStatus current, ShipmentStatus next) {
		super("Cannot transition shipment from " + current + " to " + next);
	}
}
