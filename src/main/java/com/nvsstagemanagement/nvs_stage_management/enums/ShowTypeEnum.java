package com.nvsstagemanagement.nvs_stage_management.enums;

public enum ShowTypeEnum {
    GOVERNMENT("Nhà nước"),
    ACADEMIC("Học vụ"),
    PRIVATE("Tư nhân");

    private final String displayName;

    ShowTypeEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
