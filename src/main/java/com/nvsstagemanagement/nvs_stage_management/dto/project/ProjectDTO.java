package com.nvsstagemanagement.nvs_stage_management.dto.project;

import com.nvsstagemanagement.nvs_stage_management.enums.ProjectStatus;
import lombok.Data;

import java.time.Instant;
@Data
public class ProjectDTO {
    private String projectID;
    private String title;
    private String description;
    private String content;
    private Instant startTime;
    private Instant endTime;
    private String createdBy;
    private Integer projectTypeID;
    private String projectTypeName;
    private ProjectStatus status;

}
