package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskDTO;
import com.nvsstagemanagement.nvs_stage_management.model.Asset;
import com.nvsstagemanagement.nvs_stage_management.model.Task;
import com.nvsstagemanagement.nvs_stage_management.repository.TaskRepository;
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

    @Override
    public TaskDTO createTask(TaskDTO taskDTO) {
        Task createTask = modelMapper.map(taskDTO, Task.class);
        Task savedTask = taskRepository.save(createTask);
        return modelMapper.map(savedTask, TaskDTO.class);
    }
}
