package com.nvsstagemanagement.nvs_stage_management.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
@Data
public class TaskDTO {
    private String taskID;
    @NotBlank(message = "Task title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    @Size(max = 50, message = "Priority must not exceed 50 characters")
    private String priority;
    @Size(max = 50, message = "Tag must not exceed 50 characters")
    private String tag;
    @Size(max = 2000, message = "Content must not exceed 2000 characters")
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    @NotBlank(message = "Project ID is required")
    private String projectId;
}
