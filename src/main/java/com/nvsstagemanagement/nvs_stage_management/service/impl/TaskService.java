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

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final ReturnedAssetRepository returnedAssetRepository;
    private final BorrowedAssetRepository borrowedAssetRepository;
    private final RequestAssetRepository requestAssetRepository;
    private final TaskDependencyRepository taskDependencyRepository;
    private final ModelMapper modelMapper;

    public List<TaskDTO> getAllTasksByMilestoneId(String milestoneId) {
        List<Task> tasks = taskRepository.findTasksWithUsersByMilestoneId(milestoneId);

        return tasks.stream().map(task -> {

            TaskDTO taskDTO = modelMapper.map(task, TaskDTO.class);
            List<WatcherDTO> assignedUsers = task.getTaskUsers().stream()
                    .map(taskUser -> modelMapper.map(taskUser.getUser(), WatcherDTO.class))
                    .collect(Collectors.toList());
            taskDTO.setWatchers(assignedUsers);

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

        if (updateTaskDTO.getTitle() != null && !updateTaskDTO.getTitle().trim().isEmpty()) {
            existingTask.setTitle(updateTaskDTO.getTitle());
        }
        if (updateTaskDTO.getDescription() != null && !updateTaskDTO.getDescription().trim().isEmpty()) {
            existingTask.setDescription(updateTaskDTO.getDescription());
        }
        if (updateTaskDTO.getPriority() != null && !updateTaskDTO.getPriority().trim().isEmpty()) {
            existingTask.setPriority(updateTaskDTO.getPriority());
        }
        if (updateTaskDTO.getTag() != null && !updateTaskDTO.getTag().trim().isEmpty()) {
            existingTask.setTag(updateTaskDTO.getTag());
        }
        if (updateTaskDTO.getStartDate() != null) {
            existingTask.setStartDate(updateTaskDTO.getStartDate());
        }
        if (updateTaskDTO.getEndDate() != null) {
            existingTask.setEndDate(updateTaskDTO.getEndDate());
        }
        if (updateTaskDTO.getStatus() != null && !updateTaskDTO.getStatus().trim().isEmpty()) {
            try {
                TaskEnum newStatus = TaskEnum.valueOf(updateTaskDTO.getStatus());
                existingTask.setStatus(newStatus);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: " + updateTaskDTO.getStatus());
            }
        }
        if (updateTaskDTO.getAssigneeID() != null && !updateTaskDTO.getAssigneeID().trim().isEmpty()) {
            existingTask.setAssignee(updateTaskDTO.getAssigneeID());
        }
        if (updateTaskDTO.getWatchers() != null) {
            existingTask.getTaskUsers().clear();
            taskRepository.save(existingTask);
            taskRepository.flush();
            for (WatcherDTO watcherDTO : updateTaskDTO.getWatchers()) {
                String userId = watcherDTO.getUserID();
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found: " + userId));
                TaskUserId taskUserId = new TaskUserId(existingTask.getTaskID(), userId);
                TaskUser taskUser = new TaskUser();
                taskUser.setId(taskUserId);
                taskUser.setTask(existingTask);
                taskUser.setUser(user);
                existingTask.getTaskUsers().add(taskUser);
            }
            taskRepository.save(existingTask);
        }

        if (updateTaskDTO.getUpdateBy() != null && !updateTaskDTO.getUpdateBy().trim().isEmpty()) {
            existingTask.setUpdateBy(updateTaskDTO.getUpdateBy());
        } else {
            existingTask.setUpdateBy("SYSTEM");
        }
        existingTask.setUpdateDate(LocalDateTime.now());
        Task updatedTask = taskRepository.save(existingTask);
        Task reloadedTask = taskRepository.findById(updatedTask.getTaskID())
                .orElseThrow(() -> new RuntimeException("Task not found after update"));

        List<WatcherDTO> watchers = reloadedTask.getTaskUsers() != null
                ? reloadedTask.getTaskUsers().stream()
                .map(taskUser -> modelMapper.map(taskUser.getUser(), WatcherDTO.class))
                .collect(Collectors.toList())
                : new ArrayList<>();
        UpdateTaskDTO resultDTO = modelMapper.map(reloadedTask, UpdateTaskDTO.class);
        resultDTO.setWatchers(watchers);

        return resultDTO;
    }

    @Override
    public TaskDTO getTaskByTaskId(String taskId) {
        Task task = taskRepository.findTaskWithUsersByTaskId(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        TaskDTO taskDTO = modelMapper.map(task, TaskDTO.class);

        List<WatcherDTO> watchers = task.getTaskUsers().stream()
                .map(taskUser -> modelMapper.map(taskUser.getUser(), WatcherDTO.class))
                .collect(Collectors.toList());
        taskDTO.setWatchers(watchers);
        if (task.getAssignee() != null && !task.getAssignee().isEmpty()) {
            User user = userRepository.findById(task.getAssignee())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + task.getAssignee()));
            UserDTO assigneeDto = modelMapper.map(user, UserDTO.class);
            taskDTO.setAssigneeInfo(assigneeDto);
        }
        return taskDTO;
    }

    @Override
    public TaskDTO updateTaskStatus(String taskId, String newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));

        TaskEnum targetStatus;
        try {
            targetStatus = TaskEnum.valueOf(newStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + newStatus);
        }
        validateTaskStatusFlow(task.getStatus(), targetStatus);

        if (targetStatus == TaskEnum.WorkInProgress) {
            List<TaskDependency> dependencies = taskDependencyRepository.findByTaskID(taskId);
            for (TaskDependency d : dependencies) {
                Task dependsOn = taskRepository.findById(d.getDependsOnTaskID())
                        .orElseThrow(() -> new RuntimeException("Dependent task not found"));
                if (dependsOn.getStatus() != TaskEnum.Completed) {
                    if (dependsOn.getEndDate() == null || !dependsOn.getEndDate().isBefore(LocalDate.now())) {
                        throw new RuntimeException("Cannot move to WorkInProgress: waiting for dependent task '" + dependsOn.getTitle() + "'.");
                    }
                }
            }
        }
        task.setStatus(targetStatus);
        Task updatedTask = taskRepository.save(task);
        return modelMapper.map(updatedTask, TaskDTO.class);
    }
    private void validateTaskStatusFlow(TaskEnum current, TaskEnum next) {
        switch (current) {
            case ToDo -> {
                if (next != TaskEnum.WorkInProgress) {
                    throw new RuntimeException("Can only move from ToDo to WorkInProgress");
                }
            }
            case WorkInProgress -> {
                if (next != TaskEnum.UnderReview) {
                    throw new RuntimeException("Can only move from WorkInProgress to UnderReview");
                }
            }
            case UnderReview -> {
                if (next != TaskEnum.Completed) {
                    throw new RuntimeException("Can only move from UnderReview to Completed");
                }
            }
            case Completed, Archived -> throw new RuntimeException("Cannot modify completed or archived tasks");
            default -> throw new RuntimeException("Invalid current status: " + current);
        }
    }
    @Override
    public TaskDTO addWatchersToTask(String taskId, List<WatcherDTO> WatcherDTOS) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));

        List<WatcherDTO> fullUserInfoList = new ArrayList<>();
        for (WatcherDTO inputUserDTO : WatcherDTOS) {
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
            WatcherDTO fullUserDTO = modelMapper.map(user, WatcherDTO.class);
            fullUserInfoList.add(fullUserDTO);
        }

        task.setTaskUsers(taskUserRepository.findByTask(task));
        Task updatedTask = taskRepository.save(task);
        TaskDTO updatedTaskDTO = modelMapper.map(updatedTask, TaskDTO.class);
        updatedTaskDTO.setWatchers(fullUserInfoList);
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
    @Override
    public void archiveTask(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
        boolean hasActiveRequests = requestAssetRepository.existsByTaskIdAndStatusNotIn(
                taskId,
                List.of("CANCELLED", "REJECTED")
        );
        if (hasActiveRequests) {
            throw new RuntimeException("Cannot archive task because it has active request(s).");
        }

        List<BorrowedAsset> borrowedAssets = borrowedAssetRepository.findByTask_TaskID(taskId);
        for (BorrowedAsset ba : borrowedAssets) {

            boolean isReturned = returnedAssetRepository.existsByAssetIDAndTaskID(ba.getAsset().getAssetID(), taskId);
            if (!isReturned) {
                throw new RuntimeException("Cannot archive task. Asset " + ba.getAsset().getAssetName() + " not returned.");
            }
        }
        task.setStatus(TaskEnum.Archived);
        taskRepository.save(task);
    }

    @Override
    public void permanentlyDeleteTask(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
        boolean hasActiveRequests = requestAssetRepository.existsByTaskIdAndStatusNotIn(
                taskId,
                List.of("CANCELLED", "REJECTED")
        );
        if (hasActiveRequests) {
            throw new RuntimeException("Cannot delete task because it has active request(s).");
        }
        List<BorrowedAsset> borrowedAssets = borrowedAssetRepository.findByTask_TaskID(taskId);
        for (BorrowedAsset ba : borrowedAssets) {
            boolean isReturned = returnedAssetRepository.existsByAssetIDAndTaskID(ba.getAsset().getAssetID(), taskId);
            if (!isReturned) {
                throw new RuntimeException("Cannot delete task. Asset " + ba.getAsset().getAssetName() + " not returned.");
            }
        }
        taskRepository.delete(task);
    }
    @Override
    public List<TaskDTO> getTasksByUserId(String userId) {
        List<Task> tasks = taskRepository.findTasksByUserId(userId);
        return tasks.stream().map(task -> {
            TaskDTO dto = modelMapper.map(task, TaskDTO.class);
            List<WatcherDTO> watchers = (task.getTaskUsers() == null)
                    ? new ArrayList<>()
                    : task.getTaskUsers().stream()
                    .filter(taskUser -> taskUser != null && taskUser.getUser() != null)
                    .map(taskUser -> modelMapper.map(taskUser.getUser(), WatcherDTO.class))
                    .collect(Collectors.toList());
            dto.setWatchers(watchers);
            if (task.getAssigneeUser() != null) {
                dto.setAssigneeID(task.getAssigneeUser().getId());
                dto.setAssigneeInfo(modelMapper.map(task.getAssigneeUser(), UserDTO.class));
            }

            return dto;
        }).collect(Collectors.toList());

    }

    @Override
    public List<TaskDTO> getArchivedTasks() {
        List<Task> archivedTasks = taskRepository.findByStatus(TaskEnum.Archived);

        return archivedTasks.stream().map(task -> {
            TaskDTO taskDTO = modelMapper.map(task, TaskDTO.class);

            List<WatcherDTO> watchers =
                    Optional.ofNullable(task.getTaskUsers())
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(taskUser -> modelMapper.map(taskUser.getUser(), WatcherDTO.class))
                            .collect(Collectors.toList());

            taskDTO.setWatchers(watchers);

            return taskDTO;
        }).collect(Collectors.toList());
    }

}
