package com.nvsstagemanagement.nvs_stage_management.enums;

public enum RequestAssetStatus {
    PENDING_LEADER("Pending Leader Approval"),
    LEADER_REJECTED("Leader Rejected"),
    PENDING_AM("Pending Asset Manager Approval"),
    AM_APPROVED("Asset Manager Approved"),
    REJECTED("Rejected"),
    CANCELLED("Cancelled"),
    BOOKED("Booked");
    private final String displayName;

    RequestAssetStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
