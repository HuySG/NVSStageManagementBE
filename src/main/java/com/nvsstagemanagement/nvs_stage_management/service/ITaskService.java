package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.TaskDTO;

import java.util.List;

public interface ITaskService {
    List<TaskDTO> getAllTasksByProjectId(String projectId);
}
