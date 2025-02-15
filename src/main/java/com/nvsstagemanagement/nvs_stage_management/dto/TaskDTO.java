package com.nvsstagemanagement.nvs_stage_management.dto;

import lombok.Data;

import java.time.LocalDate;
@Data
public class TaskDTO {
    private String taskID;
    private String title;
    private String description;
    private String assignee;
    private String priority;
    private String tag;
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String projectId;
}
