package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.task.AssignedUserDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskUserDTO;

import com.nvsstagemanagement.nvs_stage_management.dto.task.UpdateTaskDTO;

import com.nvsstagemanagement.nvs_stage_management.enums.TaskEnum;

import com.nvsstagemanagement.nvs_stage_management.model.*;
import com.nvsstagemanagement.nvs_stage_management.repository.ShowRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.TaskRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.TaskUserRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.UserRepository;
import com.nvsstagemanagement.nvs_stage_management.service.ITaskService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService implements ITaskService {
    private final TaskRepository taskRepository;
    private final TaskUserRepository taskUserRepository;
    private final UserRepository userRepository;
    private final ShowRepository showRepository;
    private final ModelMapper modelMapper;

    public List<TaskDTO> getAllTasksByProjectId(String projectId) {
        List<Task> tasks = taskRepository.findTasksWithUsersByShowId(projectId);
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
            throw new IllegalArgumentException("Show ID is required.");
        }
        if (taskDTO.getStatus() == null || taskDTO.getStatus().trim().isEmpty()) {
            throw new IllegalArgumentException("Status is required (e.g., 'ToDo', 'WorkInProgress', 'UnderReview', 'Completed').");
        }

        Show show = showRepository.findById(taskDTO.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Show not found: " + taskDTO.getProjectId()));

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
        task.setShow(show);
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
                .orElseThrow(() -> new RuntimeException("Task not found: " + updateTaskDTO.getTaskID()));
        if (updateTaskDTO.getTitle() != null && !updateTaskDTO.getTitle().trim().isEmpty())
            existingTask.setTitle(updateTaskDTO.getTitle());
        if (updateTaskDTO.getDescription() != null && !updateTaskDTO.getDescription().trim().isEmpty())
            existingTask.setDescription(updateTaskDTO.getDescription());
        if (updateTaskDTO.getPriority() != null && !updateTaskDTO.getPriority().trim().isEmpty())
            existingTask.setPriority(updateTaskDTO.getPriority());
        if (updateTaskDTO.getTag() != null && !updateTaskDTO.getTag().trim().isEmpty())
            existingTask.setTag(updateTaskDTO.getTag());
        if (updateTaskDTO.getStartDate() != null)
            existingTask.setStartDate(updateTaskDTO.getStartDate());
        if (updateTaskDTO.getEndDate() != null)
            existingTask.setEndDate(updateTaskDTO.getEndDate());
        if (updateTaskDTO.getAttachments() != null)
            existingTask.setAttachments(updateTaskDTO.getAttachments());
        if (updateTaskDTO.getStatus() != null && !updateTaskDTO.getStatus().trim().isEmpty()) {
            try {
                TaskEnum newStatus = TaskEnum.valueOf(updateTaskDTO.getStatus());
                existingTask.setStatus(newStatus);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: " + updateTaskDTO.getStatus());
            }
        }
        if (updateTaskDTO.getAssignedUsers() != null) {
            Set<String> newUserIds = updateTaskDTO.getAssignedUsers().stream()
                    .map(AssignedUserDTO::getUserID)
                    .collect(Collectors.toSet());
            Set<String> existingUserIds = existingTask.getTaskUsers().stream()
                    .map(taskUser -> taskUser.getUser().getId())
                    .collect(Collectors.toSet());
            if (!newUserIds.equals(existingUserIds)) {
                taskUserRepository.deleteByTask(existingTask);
                List<TaskUser> newTaskUsers = new ArrayList<>();
                for (String userId : newUserIds) {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found: " + userId));
                    TaskUserId taskUserId = new TaskUserId(existingTask.getTaskID(), userId);
                    if (taskUserRepository.existsById(taskUserId))
                        throw new RuntimeException("User " + userId + " is already assigned to this task!");
                    TaskUser taskUser = new TaskUser();
                    taskUser.setId(taskUserId);
                    taskUser.setTask(existingTask);
                    taskUser.setUser(user);
                    newTaskUsers.add(taskUser);
                }
                existingTask.setTaskUsers(newTaskUsers);
                taskUserRepository.saveAll(newTaskUsers);
            }
        }
        Task updatedTask = taskRepository.save(existingTask);
        return modelMapper.map(updatedTask, UpdateTaskDTO.class);
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
