package com.nvsstagemanagement.nvs_stage_management.enums;

public enum RequestAssetStatus {
    PENDING_LEADER("Pending Leader Approval"),
    LEADER_REJECTED("Leader Rejected"),
    PENDING_AM("Pending Asset Manager Approval"),
    AM_APPROVED("Asset Manager Approved"),
    REJECTED("Rejected"),
    CANCELLED("Cancelled"),
    BOOKED("Booked"),
    PARTIALLY_ALLOCATED("Partially Allocated"),
    FULLY_ALLOCATED("Fully Allocated"),
    FAILED_PARTIAL("Failed Partially Allocated"),
    RETURN_CONFIRMED("Return Confirmed");
    private final String displayName;

    RequestAssetStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
