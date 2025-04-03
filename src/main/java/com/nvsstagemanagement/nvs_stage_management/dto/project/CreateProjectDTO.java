package com.nvsstagemanagement.nvs_stage_management.dto.project;

import lombok.Data;

import java.time.Instant;
@Data
public class CreateProjectDTO {
    private String title;
    private String description;
    private String content;
    private Instant startTime;
    private Instant endTime;
    private String createdBy;
    private String projectTypeID;

}
