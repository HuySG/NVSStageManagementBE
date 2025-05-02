package com.nvsstagemanagement.nvs_stage_management.dto.project;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
@Data
public class CreateProjectDTO {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private String content;

    @NotNull(message = "Start time is required")
    private Instant startTime;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private Instant endTime;

    @NotBlank(message = "CreatedBy is required")
    private String createdBy;

    @NotNull(message = "ProjectTypeID is required")
    private Integer projectTypeID;

}
