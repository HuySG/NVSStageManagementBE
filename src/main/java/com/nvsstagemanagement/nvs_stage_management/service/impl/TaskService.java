package com.nvsstagemanagement.nvs_stage_management.service.impl;


import com.nvsstagemanagement.nvs_stage_management.dto.attachment.AttachmentDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.AssetPreparationDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.RequestAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.*;
import com.nvsstagemanagement.nvs_stage_management.dto.user.UserDTO;
import com.nvsstagemanagement.nvs_stage_management.enums.NotificationType;
import com.nvsstagemanagement.nvs_stage_management.enums.TaskEnum;

import com.nvsstagemanagement.nvs_stage_management.exception.ResourceNotFoundException;
import com.nvsstagemanagement.nvs_stage_management.model.*;
import com.nvsstagemanagement.nvs_stage_management.repository.*;
import com.nvsstagemanagement.nvs_stage_management.service.ITaskService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
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
    private final ReturnedAssetRepository returnedAssetRepository;
    private final BorrowedAssetRepository borrowedAssetRepository;
    private final RequestAssetRepository requestAssetRepository;
    private final RequestAssetAllocationRepository requestAssetAllocationRepository;
    private final AllocationImageRepository allocationImageRepository;
    private final ProjectRepository projectRepository;
    private final NotificationRepository notificationRepository;
    private final DepartmentProjectRepository departmentProjectRepository;
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
            Notification notification = Notification.builder()
                    .notificationID(UUID.randomUUID().toString())
                    .user(user)
                    .message("You have been assigned to task: " + task.getTitle())
                    .createDate(Instant.now())
                    .type(NotificationType.TASK_ASSIGNED)
                    .build();
            notificationRepository.save(notification);
        }

        TaskUserDTO responseDTO = new TaskUserDTO();
        responseDTO.setTaskID(taskUserDTO.getTaskID());
        responseDTO.setUserID(assignedUserIDs);
        return responseDTO;
    }

    @Override
    @Transactional
    public UpdateTaskDTO updateTask(UpdateTaskDTO dto) {
        Task existing = taskRepository.findById(dto.getTaskID())
                .orElseThrow(() -> new RuntimeException("Task not found: " + dto.getTaskID()));

        if (dto.getTitle() != null && !dto.getTitle().trim().isEmpty()) {
            existing.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null && !dto.getDescription().trim().isEmpty()) {
            existing.setDescription(dto.getDescription());
        }
        if (dto.getPriority() != null && !dto.getPriority().trim().isEmpty()) {
            existing.setPriority(dto.getPriority());
        }
        if (dto.getTag() != null && !dto.getTag().trim().isEmpty()) {
            existing.setTag(dto.getTag());
        }
        if (dto.getStartDate() != null) {
            existing.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            existing.setEndDate(dto.getEndDate());
        }
        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            try {
                existing.setStatus(TaskEnum.valueOf(dto.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: " + dto.getStatus());
            }
        }
        if (dto.getAssigneeID() != null && !dto.getAssigneeID().trim().isEmpty()) {
            String newAssignee = dto.getAssigneeID();
            String oldAssignee = existing.getAssignee();
            if (!newAssignee.equals(oldAssignee)) {
                existing.setAssignee(newAssignee);
                userRepository.findById(newAssignee).ifPresent(user -> {
                    Notification notif = Notification.builder()
                            .notificationID(UUID.randomUUID().toString())
                            .user(user)
                            .message("Bạn đã được giao nhiệm vụ: " + existing.getTitle())
                            .createDate(Instant.now())
                            .type(NotificationType.TASK_ASSIGNED)
                            .build();
                    notificationRepository.save(notif);
                });
            }
        }
        if (dto.getWatchers() != null) {
            existing.getTaskUsers().clear();
            String projectName = existing.getMilestone() != null && existing.getMilestone().getProject() != null
                    ? existing.getMilestone().getProject().getTitle()
                    : "Không xác định";
            for (WatcherDTO w : dto.getWatchers()) {
                User user = userRepository.findById(w.getUserID())
                        .orElseThrow(() -> new RuntimeException("User not found: " + w.getUserID()));
                TaskUserId tuId = new TaskUserId(existing.getTaskID(), w.getUserID());
                TaskUser tu = new TaskUser();
                tu.setId(tuId);
                tu.setTask(existing);
                tu.setUser(user);
                existing.getTaskUsers().add(tu);
                String message = String.format(
                        "Bạn đã được thêm vào tác vụ '%s' của dự án '%s'",
                        existing.getTitle(),
                        projectName
                );
                Notification notif = Notification.builder()
                        .notificationID(UUID.randomUUID().toString())
                        .user(user)
                        .message(message)
                        .createDate(Instant.now())
                        .type(NotificationType.INFO)
                        .build();
                notificationRepository.save(notif);
            }
        }

        existing.setUpdateBy(
                dto.getUpdateBy() != null && !dto.getUpdateBy().trim().isEmpty()
                        ? dto.getUpdateBy()
                        : "SYSTEM"
        );
        existing.setUpdateDate(LocalDateTime.now());

        Task saved = taskRepository.save(existing);
        UpdateTaskDTO result = modelMapper.map(saved, UpdateTaskDTO.class);
        List<WatcherDTO> watcherDTOs = saved.getTaskUsers().stream()
                .map(tu -> modelMapper.map(tu.getUser(), WatcherDTO.class))
                .collect(Collectors.toList());
        result.setWatchers(watcherDTOs);

        return result;
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
    @Transactional
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

        if (targetStatus == TaskEnum.WorkInProgress && task.getDependsOnTaskID() != null) {
            Task dep = taskRepository.findById(task.getDependsOnTaskID())
                    .orElseThrow(() -> new RuntimeException(
                            "Dependent task not found: " + task.getDependsOnTaskID()));
            if (dep.getStatus() != TaskEnum.Completed
                    && (dep.getEndDate() == null || !dep.getEndDate().isBefore(LocalDate.now()))) {
                throw new RuntimeException(
                        "Cannot move to WorkInProgress: waiting for dependent task '" + dep.getTitle() + "'."
                );
            }
        }

        task.setStatus(targetStatus);
        task.setUpdateDate(LocalDateTime.now());
        Task updated = taskRepository.save(task);

        Instant now = Instant.now();
        String title = updated.getTitle();
        String message = "Task '" + title + "' status changed to " + targetStatus;
        String assigneeId = updated.getAssignee();
        if (assigneeId != null && !assigneeId.isBlank()) {
            userRepository.findById(assigneeId).ifPresent(user -> {
                Notification notif = Notification.builder()
                        .notificationID(UUID.randomUUID().toString())
                        .user(user)
                        .message(message)
                        .createDate(now)
                        .type(NotificationType.INFO)
                        .build();
                notificationRepository.save(notif);
            });
        }
        if (updated.getTaskUsers() != null) {
            for (TaskUser tu : updated.getTaskUsers()) {
                User watcher = tu.getUser();
                Notification notif = Notification.builder()
                        .notificationID(UUID.randomUUID().toString())
                        .user(watcher)
                        .message(message)
                        .createDate(now)
                        .type(NotificationType.INFO)
                        .build();
                notificationRepository.save(notif);
            }
        }

        return modelMapper.map(updated, TaskDTO.class);
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
    @Override
    public TaskDTO createAssetPreparationTaskForRequest(String requestId, String createByUserId) {
        RequestAsset request = requestAssetRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with ID: " + requestId));
        Task preparationTask = new Task();
        preparationTask.setTaskID(UUID.randomUUID().toString());
        preparationTask.setTitle("Chuẩn bị tài sản cho yêu cầu: " + request.getTitle());
        preparationTask.setDescription("Chuẩn bị tất cả tài sản được phân bổ cho yêu cầu này.");
        preparationTask.setPriority("High");
        preparationTask.setStatus(TaskEnum.ToDo);
        preparationTask.setTag("Prepare asset");
        preparationTask.setCreateDate(LocalDateTime.now());
        preparationTask.setCreateBy(createByUserId);

        preparationTask.setStartDate(LocalDate.now());
        preparationTask.setEndDate(request.getStartTime() != null
                ? request.getStartTime().atZone(ZoneId.systemDefault()).toLocalDate()
                : LocalDate.now().plusDays(1));

        Task savedPreparationTask = taskRepository.save(preparationTask);

        if (request.getTask() != null) {
            Task mainTask = request.getTask();
            mainTask.setDependsOnTaskID(savedPreparationTask.getTaskID());
            taskRepository.save(mainTask);
        }

        return modelMapper.map(savedPreparationTask, TaskDTO.class);
    }

    @Override
    public List<TaskDTO> getTasksByProjectId(String projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);

        return tasks.stream()
                .map(task -> {
                    TaskDTO dto = modelMapper.map(task, TaskDTO.class);
                    if (task.getTaskUsers() != null) {
                        dto.setWatchers(task.getTaskUsers().stream()
                                .map(taskUser -> modelMapper.map(taskUser.getUser(), WatcherDTO.class))
                                .collect(Collectors.toList()));
                    } else {
                        dto.setWatchers(List.of());
                    }
                    if (task.getAssigneeUser() != null) {
                        dto.setAssigneeInfo(modelMapper.map(task.getAssigneeUser(), UserDTO.class));
                    } else {
                        dto.setAssigneeInfo(null);
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }
    @Override
    public List<TaskDTO> getTasksByDepartmentId(String departmentId) {
        List<Project> projects =
                departmentProjectRepository.findProjectsByDepartmentId(departmentId);
        Set<String> projectIds = projects.stream()
                .map(Project::getProjectID)
                .collect(Collectors.toSet());
        List<Task> tasks = taskRepository.findAll().stream()
                .filter(t -> t.getMilestone() != null
                        && t.getMilestone().getProject() != null
                        && projectIds.contains(t.getMilestone().getProject().getProjectID()))
                .collect(Collectors.toList());
        return tasks.stream().map(task -> {
            TaskDTO dto = modelMapper.map(task, TaskDTO.class);
            if (task.getTaskUsers() != null) {
                List<WatcherDTO> watchers = task.getTaskUsers().stream()
                        .map(tu -> modelMapper.map(tu.getUser(), WatcherDTO.class))
                        .collect(Collectors.toList());
                dto.setWatchers(watchers);
            }
            if (task.getAssigneeUser() != null) {
                dto.setAssigneeInfo(
                        modelMapper.map(task.getAssigneeUser(), UserDTO.class)
                );
            }
            return dto;
        }).collect(Collectors.toList());
    }


    @Override
    public List<TaskDTO> getPrepareTasksByProjectId(String projectId) {
        List<Task> prepareTasks = taskRepository.findPrepareTasksUsedByProject(projectId);
        return prepareTasks.stream()
                .map(task -> modelMapper.map(task, TaskDTO.class))
                .toList();
    }

    @Override
    public List<AssetPreparationDTO> getPreparationAssetsByTaskId(String taskId) {
        Task prepareTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        List<RequestAsset> requests = requestAssetRepository.findByTask(prepareTask);

        return requests.stream()
                .flatMap(request -> {
                    List<RequestAssetAllocation> allocations =
                            requestAssetAllocationRepository.findByRequestAsset(request);
                    return allocations.stream()
                            .map(alloc -> {
                                AssetPreparationDTO dto = new AssetPreparationDTO();
                                dto.setAllocationId(alloc.getAllocationId());
                                dto.setAssetId(alloc.getAsset().getAssetID());
                                dto.setAssetName(alloc.getAsset().getAssetName());
                                dto.setCategoryId(alloc.getCategory().getCategoryID());
                                dto.setCategoryName(alloc.getCategory().getName());
                                dto.setRequestId(request.getRequestId());
                                dto.setRequestTitle(request.getTitle());
                                dto.setStartTime(request.getStartTime());
                                dto.setEndTime(request.getEndTime());
                                dto.setStatus(alloc.getStatus().name());
                                dto.setConditionBefore(alloc.getConditionBefore());
                                return dto;
                            });
                })
                .collect(Collectors.toList());
    }


    @Override
    public PrepareTaskDetailDTO getPreparationDetails(String prepareTaskId) {
        Task prepareTask = taskRepository.findById(prepareTaskId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy prepare-task: " + prepareTaskId));

        Task requestTask = taskRepository.findFirstByDependsOnTaskID(prepareTaskId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy task gốc cho prepareTaskId: " + prepareTaskId));

        List<RequestAsset> requestAssets = requestAssetRepository.findByTask(requestTask);

        List<RequestAssetDTO> requestDtos = requestAssets.stream()
                .map(ra -> modelMapper.map(ra, RequestAssetDTO.class))
                .collect(Collectors.toList());

        List<AssetPreparationDTO> assetDtos = requestAssets.stream()
                .flatMap(ra -> requestAssetAllocationRepository.findByRequestAsset(ra).stream()
                        .map(alloc -> {
                            AssetPreparationDTO dto = new AssetPreparationDTO();
                            dto.setAllocationId(alloc.getAllocationId());

                            if (alloc.getAsset() != null) {
                                dto.setAssetId(alloc.getAsset().getAssetID());
                                dto.setAssetName(alloc.getAsset().getAssetName());
                            } else {
                                dto.setAssetId("PENDING_ALLOCATION");
                                dto.setAssetName("Waiting for asset");
                            }

                            dto.setCategoryId(alloc.getCategory().getCategoryID());
                            dto.setCategoryName(alloc.getCategory().getName());
                            dto.setRequestId(ra.getRequestId());
                            dto.setRequestTitle(ra.getTitle());
                            dto.setStartTime(ra.getStartTime());
                            dto.setEndTime(ra.getEndTime());
                            dto.setStatus(alloc.getStatus().name());
                            dto.setConditionBefore(alloc.getConditionBefore());
                            List<String> urls = allocationImageRepository
                                    .findByAllocation(alloc).stream()
                                    .map(AllocationImage::getImageUrl)
                                    .collect(Collectors.toList());
                            dto.setImageUrls(urls);

                            return dto;
                        })
                )
                .collect(Collectors.toList());


        PrepareTaskDetailDTO detail = new PrepareTaskDetailDTO();
        detail.setPrepareTask(modelMapper.map(prepareTask, TaskDTO.class));
        detail.setRequestTask(modelMapper.map(requestTask, TaskDTO.class));
        detail.setRequest(requestDtos);
        detail.setAssets(assetDtos);
        return detail;
    }

    @Override
    public List<ProjectWithPrepareTasksDTO> getProjectsWithPrepareTasks(String departmentId) {
        List<String> creatorIds = userRepository
                .findByDepartment_DepartmentId(departmentId)
                .stream()
                .map(User::getId)
                .collect(Collectors.toList());

        if (creatorIds.isEmpty()) {
            return Collections.emptyList();
        }

        return projectRepository.findAll().stream()
                .map(project -> {
                    List<TaskDTO> tasks = taskRepository
                            .findPrepareTasksUsedByProject(project.getProjectID())
                            .stream()
                            .filter(t -> creatorIds.contains(t.getCreateBy()))
                            .map(t -> modelMapper.map(t, TaskDTO.class))
                            .collect(Collectors.toList());
                    if (tasks.isEmpty()) {
                        return null;
                    }
                    ProjectWithPrepareTasksDTO dto = new ProjectWithPrepareTasksDTO();
                    dto.setProjectId(project.getProjectID());
                    dto.setProjectTitle(project.getTitle());
                    dto.setPrepareTasks(tasks);
                    return dto;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectWithPrepareTasksDTO> getProjectsWithPrepareTasksByAssignee(String assigneeId) {
        if (!userRepository.existsById(assigneeId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found: " + assigneeId
            );
        }

        return projectRepository.findAll().stream()
                .map(project -> {
                    List<Task> rawTasks = taskRepository.findPrepareTasksUsedByProject(project.getProjectID());
                    List<TaskDTO> tasks = rawTasks.stream()
                            .filter(t -> assigneeId.equals(t.getAssignee()))
                            .map(t -> {
                                TaskDTO dto = modelMapper.map(t, TaskDTO.class);
                                if (t.getAssigneeUser() != null) {
                                    UserDTO u = modelMapper.map(t.getAssigneeUser(), UserDTO.class);
                                    dto.setAssigneeInfo(u);
                                }
                                return dto;
                            })
                            .collect(Collectors.toList());

                    if (tasks.isEmpty()) {
                        return null;
                    }
                    ProjectWithPrepareTasksDTO dto = new ProjectWithPrepareTasksDTO();
                    dto.setProjectId(project.getProjectID());
                    dto.setProjectTitle(project.getTitle());
                    dto.setPrepareTasks(tasks);
                    return dto;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


}
