package com.nvsstagemanagement.nvs_stage_management.dto.milestone;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class MilestoneDTO {
    private String milestoneID;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String projectID;
}
