package com.eworld.enumstype;

public enum DeliveryStatus {
    PENDING("Pending"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    AWAITINGPAYMENT("Awaiting Payment"),
    AWAITINGPICKUP("Awaiting Pickup"),
    PARTIALLYSHIPPED("Partially Shipped"),
	ALL("All");
    
    private final String status;

	private DeliveryStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
	
}
