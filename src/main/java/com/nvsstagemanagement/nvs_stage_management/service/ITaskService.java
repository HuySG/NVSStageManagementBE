package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskUserDTO;

import java.util.List;

public interface ITaskService {
    List<TaskDTO> getAllTasksByProjectId(String projectId);
    TaskDTO createTask(TaskDTO taskDTO);
    TaskUserDTO assignUserToTask(TaskUserDTO taskUserDTO);
    TaskDTO updateTask (TaskDTO taskDTO);
}
