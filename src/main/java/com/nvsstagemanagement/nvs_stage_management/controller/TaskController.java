package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskUserDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.UpdateStatusDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.UpdateTaskDTO;
import com.nvsstagemanagement.nvs_stage_management.service.ITaskService;
import com.nvsstagemanagement.nvs_stage_management.service.impl.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final ITaskService taskService;

    @GetMapping("/showId")
    public ResponseEntity<List<TaskDTO>> getAllTasksByShowId(@RequestParam String showId) {
        List<TaskDTO> tasks = taskService.getAllTasksByShowId(showId);
        return ResponseEntity.ok(tasks);
    }
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO taskDTO) {
        TaskDTO createdTask = taskService.createTask(taskDTO);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }
    @PostMapping("/assign")
    public ResponseEntity<TaskUserDTO> assignUserToTask(@RequestBody TaskUserDTO taskUserDTO) {
        TaskUserDTO taskUser = taskService.assignUserToTask(taskUserDTO);
        return ResponseEntity.ok(taskUser);
    }
    @PutMapping
    public ResponseEntity<UpdateTaskDTO> updateTask(@RequestBody UpdateTaskDTO updateTaskDTO){
        UpdateTaskDTO updateTask = taskService.updateTask(updateTaskDTO);
        return ResponseEntity.ok(updateTask);
    }
    @GetMapping("/taskId")
    public ResponseEntity<TaskDTO> getTaskByTaskId(@RequestParam String taskId) {
        return ResponseEntity.ok(taskService.getTaskByTaskId(taskId));
    }
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskDTO> updateTaskStatus(
            @PathVariable String taskId,
            @RequestBody UpdateStatusDTO updateStatusDTO
    ) {
        TaskDTO updatedTask = taskService.updateTaskStatus(taskId, updateStatusDTO.getStatus());
        return ResponseEntity.ok(updatedTask);
    }

}
