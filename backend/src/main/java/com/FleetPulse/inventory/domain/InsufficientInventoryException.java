package com.FleetPulse.inventory.domain;

public class InsufficientInventoryException extends RuntimeException {
	public InsufficientInventoryException(String sku) { super("Insufficient inventory for SKU " + sku); }
}
