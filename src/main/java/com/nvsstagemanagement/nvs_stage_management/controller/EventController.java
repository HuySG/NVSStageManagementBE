package com.nvsstagemanagement.nvs_stage_management.controller;
import com.nvsstagemanagement.nvs_stage_management.dto.event.EventDTO;
import com.nvsstagemanagement.nvs_stage_management.service.IEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/v1/event")
@RequiredArgsConstructor
public class EventController {
    private final IEventService eventService;

    @GetMapping("/projectId")
    public ResponseEntity<List<EventDTO>> getAllEventsByProjectId(@RequestParam String projectId) {
        List<EventDTO> events = eventService.getEventsByProjectID(projectId);
        return ResponseEntity.ok(events);
    }
    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventDTO eventDTO){
        EventDTO createdEvent = eventService.createEvent(eventDTO);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<EventDTO>> getAll() {
        List<EventDTO> events = eventService.getAll();
        return ResponseEntity.ok(events);
    }
}
