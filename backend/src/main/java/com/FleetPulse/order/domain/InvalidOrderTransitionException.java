package com.FleetPulse.order.domain;

public class InvalidOrderTransitionException extends RuntimeException {
	public InvalidOrderTransitionException(OrderStatus current, OrderStatus next) { super("Cannot transition order from " + current + " to " + next); }
}
