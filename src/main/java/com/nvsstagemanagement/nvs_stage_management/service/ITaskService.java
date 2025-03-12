package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskUserDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.UpdateTaskDTO;

import java.util.List;

public interface ITaskService {
    List<TaskDTO> getAllTasksByShowId(String projectId);
    TaskDTO createTask(TaskDTO taskDTO);
    TaskUserDTO assignUserToTask(TaskUserDTO taskUserDTO);
    UpdateTaskDTO updateTask (UpdateTaskDTO updateTaskDTO);
    TaskDTO getTaskByTaskId(String taskId);
    TaskDTO updateTaskStatus(String taskId, String newStatus);
}
