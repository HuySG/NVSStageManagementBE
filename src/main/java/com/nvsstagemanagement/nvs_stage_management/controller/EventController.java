package com.nvsstagemanagement.nvs_stage_management.controller;
import com.nvsstagemanagement.nvs_stage_management.dto.event.EventDTO;
import com.nvsstagemanagement.nvs_stage_management.service.IEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api/v1/event")
@RequiredArgsConstructor
public class EventController {
    private final IEventService eventService;

    @GetMapping("/{projectId}")
    public ResponseEntity<List<EventDTO>> getAllTasksByProjectId(@PathVariable String projectId) {
        List<EventDTO> events = eventService.getEventsByProjectID(projectId);
        return ResponseEntity.ok(events);
    }
}
