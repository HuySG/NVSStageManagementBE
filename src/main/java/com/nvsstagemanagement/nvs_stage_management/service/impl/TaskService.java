package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskUserDTO;
import com.nvsstagemanagement.nvs_stage_management.enums.TaskEnum;
import com.nvsstagemanagement.nvs_stage_management.model.*;
import com.nvsstagemanagement.nvs_stage_management.repository.ProjectRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.TaskRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.TaskUserRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.UserRepository;
import com.nvsstagemanagement.nvs_stage_management.service.ITaskService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService implements ITaskService {
    private final TaskRepository taskRepository;
    private final TaskUserRepository taskUserRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;

    public List<TaskDTO> getAllTasksByProjectId(String projectId) {
        List<Task> tasks = taskRepository.findByProject_ProjectID(projectId);
        return tasks.stream()
                .map(task -> modelMapper.map(task, TaskDTO.class)).toList();
    }

    @Override
    public TaskDTO createTask(TaskDTO taskDTO) {

        if (taskDTO == null) {
            throw new IllegalArgumentException("Task data is required.");
        }
        if (taskDTO.getProjectId() == null || taskDTO.getProjectId().trim().isEmpty()) {
            throw new IllegalArgumentException("Project ID is required.");
        }
        if (taskDTO.getStatus() == null || taskDTO.getStatus().trim().isEmpty()) {
            throw new IllegalArgumentException("Status is required (e.g., 'ToDo', 'WorkInProgress', 'UnderReview', 'Completed').");
        }

        Project project = projectRepository.findById(taskDTO.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + taskDTO.getProjectId()));

        TaskEnum taskStatus;
        try {
            taskStatus = TaskEnum.valueOf(taskDTO.getStatus());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + taskDTO.getStatus());
        }
        Task task = modelMapper.map(taskDTO, Task.class);

        if (task.getTaskID() == null || task.getTaskID().trim().isEmpty()) {
            task.setTaskID(UUID.randomUUID().toString());
        }
        task.setStatus(taskStatus);

        Task savedTask = taskRepository.save(task);
        return modelMapper.map(savedTask, TaskDTO.class);

    }

    @Override
    public TaskUserDTO assignUserToTask(TaskUserDTO taskUserDTO) {
        Task task = taskRepository.findById(taskUserDTO.getTaskID())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        User user = userRepository.findById(taskUserDTO.getUserID())
                .orElseThrow(() -> new RuntimeException("User not found"));

        TaskUserId taskUserId = new TaskUserId(taskUserDTO.getTaskID(), taskUserDTO.getUserID());

        if (taskUserRepository.existsById(taskUserId)) {
            throw new RuntimeException("User is already assigned to this task!");
        }

        TaskUser taskUser = new TaskUser();
        taskUser.setId(taskUserId);
        taskUser.setTask(task);
        taskUser.setUser(user);

        taskUserRepository.save(taskUser);

        TaskUserDTO responseDTO = new TaskUserDTO();
        responseDTO.setTaskID(taskUserDTO.getTaskID());
        responseDTO.setUserID(taskUserDTO.getUserID());
        return responseDTO;
    }
}
