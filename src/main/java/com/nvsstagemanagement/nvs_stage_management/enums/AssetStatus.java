package com.nvsstagemanagement.nvs_stage_management.enums;

public enum AssetStatus {
    AVAILABLE("Available"),
    UNAVAILABLE("Unavailable");

    private final String displayName;

    AssetStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
