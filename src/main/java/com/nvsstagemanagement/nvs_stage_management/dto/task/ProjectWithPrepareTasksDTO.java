package com.nvsstagemanagement.nvs_stage_management.dto.task;

import lombok.Data;

import java.util.List;

@Data
public class ProjectWithPrepareTasksDTO {
    private String projectId;
    private String projectTitle;
    private List<TaskDTO> prepareTasks;
}
