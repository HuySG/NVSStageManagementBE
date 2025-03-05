package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.task.AssignedUserDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskUserDTO;

import com.nvsstagemanagement.nvs_stage_management.dto.task.UpdateTaskDTO;

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

import java.util.ArrayList;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

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
        List<Task> tasks = taskRepository.findTasksWithUsersByProjectId(projectId);
        return tasks.stream().map(task -> {
            TaskDTO taskDTO = modelMapper.map(task, TaskDTO.class);
            List<AssignedUserDTO> assignedUsers = task.getTaskUsers().stream()
                    .map(taskUser -> modelMapper.map(taskUser.getUser(), AssignedUserDTO.class))
                    .collect(Collectors.toList());
            taskDTO.setAssignedUsers(assignedUsers);
            return taskDTO;
        }).collect(Collectors.toList());
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
        task.setProject(project);
        task.setStatus(taskStatus);
        task.setTaskUsers(new ArrayList<>());
        Task savedTask = taskRepository.save(task);
        TaskDTO savedTaskDTO = modelMapper.map(savedTask, TaskDTO.class);

        if (taskDTO.getAssignedUsers() != null && !taskDTO.getAssignedUsers().isEmpty()) {
            List<AssignedUserDTO> fullUserInfoList = new ArrayList<>();
            for (AssignedUserDTO inputUserDTO : taskDTO.getAssignedUsers()) {
                String userID = inputUserDTO.getUserID();
                User user = userRepository.findById(userID)
                        .orElseThrow(() -> new RuntimeException("User not found: " + userID));
                TaskUserId taskUserId = new TaskUserId(savedTask.getTaskID(), userID);
                if (taskUserRepository.existsById(taskUserId)) {
                    throw new RuntimeException("User " + userID + " is already assigned to this task!");
                }
                TaskUser taskUser = new TaskUser();
                taskUser.setId(taskUserId);
                taskUser.setTask(savedTask);
                taskUser.setUser(user);
                taskUserRepository.save(taskUser);
                AssignedUserDTO fullUserDTO = modelMapper.map(user, AssignedUserDTO.class);
                fullUserInfoList.add(fullUserDTO);
            }
            savedTaskDTO.setAssignedUsers(fullUserInfoList);
        }

            return savedTaskDTO;

    }

    @Override
    public TaskUserDTO assignUserToTask(TaskUserDTO taskUserDTO) {

        Task task = taskRepository.findById(taskUserDTO.getTaskID())
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskUserDTO.getTaskID()));

        List<String> assignedUserIDs = new ArrayList<>();

        for (String userID : taskUserDTO.getUserID()) {

            User user = userRepository.findById(userID)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userID));


            TaskUserId taskUserId = new TaskUserId(taskUserDTO.getTaskID(), userID);


            if (taskUserRepository.existsById(taskUserId)) {
                throw new RuntimeException("User " + userID + " is already assigned to this task!");
            }

            TaskUser taskUser = new TaskUser();
            taskUser.setId(taskUserId);
            taskUser.setTask(task);
            taskUser.setUser(user);
            taskUserRepository.save(taskUser);

            assignedUserIDs.add(userID);
        }

        TaskUserDTO responseDTO = new TaskUserDTO();
        responseDTO.setTaskID(taskUserDTO.getTaskID());
        responseDTO.setUserID(assignedUserIDs);
        return responseDTO;
    }

    @Override
    public UpdateTaskDTO updateTask(UpdateTaskDTO updateTaskDTO) {
            Task existingTask = taskRepository.findById(updateTaskDTO.getTaskID())
                    .orElseThrow(() -> new RuntimeException("Task not found"));
            taskRepository.save(existingTask);

    return modelMapper.map(existingTask,UpdateTaskDTO.class);

    }

    @Override
    public TaskDTO getTaskByTaskId(String taskId) {
        Task task = taskRepository.findTaskWithUsersByTaskId(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        TaskDTO taskDTO = modelMapper.map(task, TaskDTO.class);

        List<AssignedUserDTO> assignedUsers = task.getTaskUsers().stream()
                .map(taskUser -> modelMapper.map(taskUser.getUser(), AssignedUserDTO.class))
                .collect(Collectors.toList());

        taskDTO.setAssignedUsers(assignedUsers);
        return taskDTO;
    }

    @Override
    public TaskDTO updateTaskStatus(String taskId, String newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));

        TaskEnum taskEnum;
        try {
            taskEnum = TaskEnum.valueOf(newStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + newStatus);
        }

        task.setStatus(taskEnum);
        Task updatedTask = taskRepository.save(task);
        return modelMapper.map(updatedTask, TaskDTO.class);
    }
}
