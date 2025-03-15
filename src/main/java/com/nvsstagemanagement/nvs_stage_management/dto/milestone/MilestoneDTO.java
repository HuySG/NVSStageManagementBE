package com.nvsstagemanagement.nvs_stage_management.dto.milestone;

import lombok.Data;

import java.time.Instant;

@Data
public class MilestoneDTO {
    private String milestoneID;
    private String title;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private String projectID;
}
