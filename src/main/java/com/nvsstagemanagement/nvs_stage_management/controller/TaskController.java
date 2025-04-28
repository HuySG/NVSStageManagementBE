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
            @RequestBody List<WatcherDTO> watchers) {
        try {
            TaskDTO updatedTask = taskService.addWatchersToTask(taskId, watchers);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }


    @GetMapping("/by-user/{userId}")
    public ResponseEntity<?> getTasksByUserId(@PathVariable String userId) {
        try {
            List<TaskDTO> tasks = taskService.getTasksByUserId(userId);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }
    @PostMapping("/archive/taskId")
    public ResponseEntity<?> archiveTask(@RequestParam String id) {
        try {
            taskService.archiveTask(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    @DeleteMapping("/delete/taskId")
    public ResponseEntity<?> permanentlyDeleteTask(@RequestParam String id) {
        try {
            taskService.permanentlyDeleteTask(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    @GetMapping("/archive")
    public ResponseEntity<List<TaskDTO>> getArchivedTasks() {
        List<TaskDTO> archivedTasks = taskService.getArchivedTasks();
        if (archivedTasks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(archivedTasks);
    }
    /**
     * API để tự động tạo Task "Chuẩn bị tài sản" cho staff AM
     *
     * @param requestId ID của RequestAsset
     * @return TaskDTO của task chuẩn bị tài sản vừa tạo
     */
    @PostMapping("/prepare-asset/{requestId}")
    public ResponseEntity<?> createPreparationTask(@PathVariable String requestId) {
        try {
            TaskDTO preparationTask = taskService.createAssetPreparationTaskForRequest(requestId);
            return ResponseEntity.ok(preparationTask);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Bad Request: " + ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + ex.getMessage());
        }
    }
}
