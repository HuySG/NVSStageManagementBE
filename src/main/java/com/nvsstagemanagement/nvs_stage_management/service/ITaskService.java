package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.attachment.AttachmentDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.AssetPreparationDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.*;
import com.nvsstagemanagement.nvs_stage_management.enums.TaskEnum;

import java.util.List;

public interface ITaskService {
    List<TaskDTO> getAllTasksByMilestoneId(String milestoneId);
    TaskDTO createTask(CreateTaskDTO createTaskDTO);
    TaskUserDTO assignUserToTask(TaskUserDTO taskUserDTO);
    UpdateTaskDTO updateTask (UpdateTaskDTO updateTaskDTO);
    TaskDTO getTaskByTaskId(String taskId);
    TaskDTO updateTaskStatus(String taskId, String newStatus);
    TaskDTO addAttachmentsToTask(String taskId, List<AttachmentDTO> attachmentDTOs);
    TaskDTO addWatchersToTask(String taskId, List<WatcherDTO> WatcherDTOS);
    void archiveTask(String taskId);
    void permanentlyDeleteTask(String taskId);
    List<TaskDTO> getTasksByUserId(String userId);
    List<TaskDTO> getArchivedTasks();
    TaskDTO createAssetPreparationTaskForRequest(String requestId, String createByUserId);
    List<TaskDTO> getTasksByProjectId(String projectId);
    List<TaskDTO> getTasksByDepartmentId(String departmentId);
    List<TaskDTO> getPrepareTasksByProjectId(String projectId);
    List<AssetPreparationDTO> getPreparationAssetsByTaskId(String taskId);
    PrepareTaskDetailDTO getPreparationDetails(String prepareTaskId);

}
