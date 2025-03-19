package com.nvsstagemanagement.nvs_stage_management.dto.task;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
@Data
public class CreateTaskDTO {
    @NotBlank(message = "Title is required.")
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    @NotBlank(message = "Milestone ID is required.")
    private String milestoneId;
    @NotBlank(message = "Status is required.")
    private String status;
    private String createBy;
}
