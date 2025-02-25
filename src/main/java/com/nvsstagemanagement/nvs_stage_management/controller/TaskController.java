package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskUserDTO;
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

    @GetMapping("/projectId")
    public ResponseEntity<List<TaskDTO>> getAllTasksByProjectId(@RequestParam String projectId) {
        List<TaskDTO> tasks = taskService.getAllTasksByProjectId(projectId);
        return ResponseEntity.ok(tasks);
    }
    @PostMapping
    public ResponseEntity<TaskDTO> createAsset(@RequestBody TaskDTO taskDTO) {
        TaskDTO createdTask = taskService.createTask(taskDTO);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }
    @PostMapping("/assign")
    public ResponseEntity<TaskUserDTO> assignUserToTask(@RequestBody TaskUserDTO taskUserDTO) {
        TaskUserDTO taskUser = taskService.assignUserToTask(taskUserDTO);
        return ResponseEntity.ok(taskUser);
    }

}
