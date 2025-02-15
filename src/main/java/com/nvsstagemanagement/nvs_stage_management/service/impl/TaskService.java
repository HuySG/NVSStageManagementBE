package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.ProjectDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.TaskDTO;
import com.nvsstagemanagement.nvs_stage_management.model.Task;
import com.nvsstagemanagement.nvs_stage_management.repository.ProjectRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.TaskRepository;
import com.nvsstagemanagement.nvs_stage_management.service.IAssetService;
import com.nvsstagemanagement.nvs_stage_management.service.ITaskService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class TaskService implements ITaskService {
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    public List<TaskDTO> getAllTasksByProjectId(String projectId) {
        List<Task> tasks = taskRepository.findByProject_ProjectID(projectId);
        return tasks.stream()
                .map(task -> modelMapper.map(task, TaskDTO.class)).toList();
    }
}
