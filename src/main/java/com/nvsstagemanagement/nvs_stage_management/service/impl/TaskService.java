package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskUserDTO;
import com.nvsstagemanagement.nvs_stage_management.model.*;
import com.nvsstagemanagement.nvs_stage_management.repository.TaskRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.TaskUserRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.UserRepository;
import com.nvsstagemanagement.nvs_stage_management.service.ITaskService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class TaskService implements ITaskService {
    private final TaskRepository taskRepository;
    private final TaskUserRepository taskUserRepository;
    private final UserRepository userRepository;

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
