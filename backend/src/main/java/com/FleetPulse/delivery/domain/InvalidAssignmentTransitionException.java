package com.FleetPulse.delivery.domain;

public class InvalidAssignmentTransitionException extends RuntimeException {
	public InvalidAssignmentTransitionException(AssignmentStatus current, AssignmentStatus next) { super("Cannot transition assignment from " + current + " to " + next); }
}
