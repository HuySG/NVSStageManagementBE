package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.attachment.AttachmentDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.*;
import com.nvsstagemanagement.nvs_stage_management.service.ITaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final ITaskService taskService;

    @GetMapping("/milestoneId")
    public ResponseEntity<List<TaskDTO>> getAllTasksByMilestoneId(@RequestParam String milestoneId) {
        List<TaskDTO> tasks = taskService.getAllTasksByMilestoneId(milestoneId);
        return ResponseEntity.ok(tasks);
    }
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody CreateTaskDTO createTaskDTO) {
        try {
            TaskDTO createdTask = taskService.createTask(createTaskDTO);
            return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/assign")
    public ResponseEntity<TaskUserDTO> assignUserToTask(@RequestBody TaskUserDTO taskUserDTO) {
        TaskUserDTO taskUser = taskService.assignUserToTask(taskUserDTO);
        return ResponseEntity.ok(taskUser);
    }
    @PutMapping
    public ResponseEntity<?> updateTask(@RequestBody UpdateTaskDTO updateTaskDTO){
        try{
            UpdateTaskDTO updateTask = taskService.updateTask(updateTaskDTO);
            return ResponseEntity.ok(updateTask);
        }catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

    }
    @GetMapping("/taskId")
    public ResponseEntity<TaskDTO> getTaskByTaskId(@RequestParam String taskId) {
        return ResponseEntity.ok(taskService.getTaskByTaskId(taskId));
    }
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<?> updateTaskStatus(
            @PathVariable String taskId,
            @RequestBody UpdateStatusDTO updateStatusDTO
    ) {
        try {
            TaskDTO updatedTask = taskService.updateTaskStatus(taskId, updateStatusDTO.getStatus());
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    @PostMapping("/{taskId}/attachments")
    public ResponseEntity<?> addAttachmentsToTask(
            @RequestParam String taskId,
            @RequestBody List<AttachmentDTO> attachments) {
        try {
            TaskDTO updatedTask = taskService.addAttachmentsToTask(taskId, attachments);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/{taskId}/watchers")
    public ResponseEntity<?> addWatchersToTask(
            @RequestParam String taskId,
            @RequestBody List<watcherDTO> watchers) {
        try {
            TaskDTO updatedTask = taskService.addWatchersToTask(taskId, watchers);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

}
