package com.nvsstagemanagement.nvs_stage_management.dto.project;

import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskDTO;
import lombok.Data;

import java.util.List;
@Data
public class ProjectTaskDTO extends ProjectDTO{
    private List<TaskDTO> tasks;
}
