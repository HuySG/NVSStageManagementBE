package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.attachment.AttachmentDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.*;

import java.util.List;

public interface ITaskService {
    List<TaskDTO> getAllTasksByMilestoneId(String projectId);
    TaskDTO createTask(CreateTaskDTO createTaskDTO);
    TaskUserDTO assignUserToTask(TaskUserDTO taskUserDTO);
    UpdateTaskDTO updateTask (UpdateTaskDTO updateTaskDTO);
    TaskDTO getTaskByTaskId(String taskId);
    TaskDTO updateTaskStatus(String taskId, String newStatus);
    TaskDTO addAttachmentsToTask(String taskId, List<AttachmentDTO> attachmentDTOs);
    TaskDTO addWatchersToTask(String taskId, List<watcherDTO> watcherDTOs);
}
