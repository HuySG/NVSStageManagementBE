package com.nvsstagemanagement.nvs_stage_management.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;


public enum TaskEnum {
    ToDo("To Do"),
    WorkInProgress("Work In Progress"),
    UnderReview("Under Review"),
    Completed("Completed");

    private final String displayName;
    TaskEnum(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
}

