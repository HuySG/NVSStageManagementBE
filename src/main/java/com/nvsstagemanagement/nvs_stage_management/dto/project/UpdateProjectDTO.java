package com.nvsstagemanagement.nvs_stage_management.dto.project;

import com.nvsstagemanagement.nvs_stage_management.enums.ProjectStatus;
import lombok.Data;

import java.time.Instant;

@Data
public class UpdateProjectDTO {
    private String title;
    private String description;
    private String content;
    private Instant startTime;
    private Instant endTime;
    private String projectTypeID;
    private ProjectStatus status;
}
