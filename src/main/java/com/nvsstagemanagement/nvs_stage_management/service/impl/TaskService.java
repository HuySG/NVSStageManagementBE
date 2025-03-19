package com.nvsstagemanagement.nvs_stage_management.service.impl;


import com.nvsstagemanagement.nvs_stage_management.dto.attachment.AttachmentDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.*;
import com.nvsstagemanagement.nvs_stage_management.dto.user.UserDTO;
import com.nvsstagemanagement.nvs_stage_management.enums.TaskEnum;

import com.nvsstagemanagement.nvs_stage_management.model.*;
import com.nvsstagemanagement.nvs_stage_management.repository.*;
import com.nvsstagemanagement.nvs_stage_management.service.ITaskService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService implements ITaskService {
    private final TaskRepository taskRepository;
    private final TaskUserRepository taskUserRepository;
    private final UserRepository userRepository;
    private final AttachmentRepository attachmentRepository;
    private final MilestoneRepository milestoneRepository;
    private final ModelMapper modelMapper;

    public List<TaskDTO> getAllTasksByMilestoneId(String milestoneId) {
        List<Task> tasks = taskRepository.findTasksWithUsersByMilestoneId(milestoneId);

        return tasks.stream().map(task -> {

            TaskDTO taskDTO = modelMapper.map(task, TaskDTO.class);
            List<watcherDTO> assignedUsers = task.getTaskUsers().stream()
                    .map(taskUser -> modelMapper.map(taskUser.getUser(), watcherDTO.class))
                    .collect(Collectors.toList());
            taskDTO.setWatcher(assignedUsers);

            if (task.getAssignee() != null && !task.getAssignee().trim().isEmpty()) {
                User assigneeUser = userRepository.findById(task.getAssignee())
                        .orElse(null);
                if (assigneeUser != null) {
                    UserDTO assigneeInfo = modelMapper.map(assigneeUser, UserDTO.class);
                    taskDTO.setAssigneeInfo(assigneeInfo);
                }
            }

            return taskDTO;
        }).collect(Collectors.toList());
    }


    @Override
    public TaskDTO createTask(CreateTaskDTO createTaskDTO) {
        if (createTaskDTO == null) {
            throw new IllegalArgumentException("Task data is required.");
        }
        if (createTaskDTO.getMilestoneId() == null || createTaskDTO.getMilestoneId().trim().isEmpty()) {
            throw new IllegalArgumentException("Milestone ID is required.");
        }
        if (createTaskDTO.getStatus() == null || createTaskDTO.getStatus().trim().isEmpty()) {
            throw new IllegalArgumentException("Status is required (e.g., 'ToDo', 'WorkInProgress', 'UnderReview', 'Completed').");
        }

        Milestone milestone = milestoneRepository.findById(createTaskDTO.getMilestoneId())
                .orElseThrow(() -> new IllegalArgumentException("Milestone not found: " + createTaskDTO.getMilestoneId()));


        if (createTaskDTO.getStartDate() != null && createTaskDTO.getEndDate() != null) {
            if (createTaskDTO.getStartDate().isBefore(milestone.getStartDate())) {
                throw new IllegalArgumentException("Task start time cannot be before the milestone start time ("
                        + milestone.getStartDate() + ").");
            }
            if (createTaskDTO.getEndDate().isAfter(milestone.getEndDate())) {
                throw new IllegalArgumentException("Task end time cannot be after the milestone end time ("
                        + milestone.getEndDate() + ").");
            }
        }
        TaskEnum taskStatus;
        try {
            taskStatus = TaskEnum.valueOf(createTaskDTO.getStatus());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + createTaskDTO.getStatus());
        }

        Task task = modelMapper.map(createTaskDTO, Task.class);
        task.setTaskID(UUID.randomUUID().toString());
        task.setMilestone(milestone);
        task.setStatus(taskStatus);
        if (createTaskDTO.getCreateBy() != null && !createTaskDTO.getCreateBy().trim().isEmpty()) {
            task.setCreateBy(createTaskDTO.getCreateBy());
        }
        task.setCreateDate(LocalDateTime.now());
        task.setTaskUsers(new ArrayList<>());
        task.setAttachments(new ArrayList<>());
        taskRepository.save(task);
        TaskDTO savedTaskDTO = modelMapper.map(task, TaskDTO.class);

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
                    .map(watcherDTO::getUserID)
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
        if (updateTaskDTO.getUpdateBy() != null && !updateTaskDTO.getUpdateBy().trim().isEmpty()) {
            existingTask.setUpdateBy(updateTaskDTO.getUpdateBy());
        } else {
            existingTask.setUpdateBy("SYSTEM");
        }
        existingTask.setUpdateDate(LocalDateTime.now());
        Task updatedTask = taskRepository.save(existingTask);
        return modelMapper.map(updatedTask, UpdateTaskDTO.class);
    }

    @Override
    public TaskDTO getTaskByTaskId(String taskId) {
        Task task = taskRepository.findTaskWithUsersByTaskId(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        TaskDTO taskDTO = modelMapper.map(task, TaskDTO.class);

        List<watcherDTO> assignedUsers = task.getTaskUsers().stream()
                .map(taskUser -> modelMapper.map(taskUser.getUser(), watcherDTO.class))
                .collect(Collectors.toList());

        taskDTO.setWatcher(assignedUsers);
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
    @Override
    public TaskDTO addWatchersToTask(String taskId, List<watcherDTO> watcherDTOs) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));

        List<watcherDTO> fullUserInfoList = new ArrayList<>();
        for (watcherDTO inputUserDTO : watcherDTOs) {
            String userID = inputUserDTO.getUserID();
            User user = userRepository.findById(userID)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userID));
            TaskUserId taskUserId = new TaskUserId(task.getTaskID(), userID);
            if (taskUserRepository.existsById(taskUserId)) {
                throw new RuntimeException("User " + userID + " is already assigned to this task!");
            }
            TaskUser taskUser = new TaskUser();
            taskUser.setId(taskUserId);
            taskUser.setTask(task);
            taskUser.setUser(user);
            taskUserRepository.save(taskUser);
            watcherDTO fullUserDTO = modelMapper.map(user, watcherDTO.class);
            fullUserInfoList.add(fullUserDTO);
        }

        task.setTaskUsers(taskUserRepository.findByTask(task));
        Task updatedTask = taskRepository.save(task);
        TaskDTO updatedTaskDTO = modelMapper.map(updatedTask, TaskDTO.class);
        updatedTaskDTO.setWatcher(fullUserInfoList);
        return updatedTaskDTO;
    }
    @Override
    public TaskDTO addAttachmentsToTask(String taskId, List<AttachmentDTO> attachmentDTOs) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));

        List<AttachmentDTO> attachmentDTOList = new ArrayList<>();
        for (AttachmentDTO attachmentDTO : attachmentDTOs) {
            Attachment attachment = modelMapper.map(attachmentDTO, Attachment.class);
            if (attachment.getAttachmentId() == null || attachment.getAttachmentId().trim().isEmpty()) {
                attachment.setAttachmentId(UUID.randomUUID().toString());
            }
            attachment.setTask(task);
            Attachment savedAttachment = attachmentRepository.save(attachment);
            attachmentDTOList.add(modelMapper.map(savedAttachment, AttachmentDTO.class));
        }

        task.setAttachments(attachmentRepository.findByTask(task));
        Task updatedTask = taskRepository.save(task);
        return modelMapper.map(updatedTask, TaskDTO.class);
    }

}
