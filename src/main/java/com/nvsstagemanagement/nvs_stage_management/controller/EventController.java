package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.event.CreateEventDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.event.EventDTO;
import com.nvsstagemanagement.nvs_stage_management.service.IEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {
    private final IEventService eventService;

    @PostMapping
    public ResponseEntity<?> createEvent(@Valid @RequestBody CreateEventDTO createEventDTO) {
        try {
            EventDTO created = eventService.createEvent(createEventDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
    @GetMapping("/{eventID}")
    public ResponseEntity<?> getEventById(@RequestParam String eventID) {
        try {
            EventDTO eventDTO = eventService.getEventById(eventID);
            return ResponseEntity.ok(eventDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }
    @PutMapping("/{eventID}")
    public ResponseEntity<?> updateEvent(
            @RequestParam String eventID,
            @Valid @RequestBody CreateEventDTO createEventDTO) {
        try {
            EventDTO updated = eventService.updateEvent(eventID, createEventDTO);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
    @DeleteMapping("/{eventID}")
    public ResponseEntity<?> deleteEvent(@RequestParam String eventID) {
        try {
            eventService.deleteEvent(eventID);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllEvents() {
        try {
            return ResponseEntity.ok(eventService.getAllEvents());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    @GetMapping("/milestone/{milestoneId}")
    public ResponseEntity<?> getEventsByMilestone(@PathVariable("milestoneId") String milestoneId) {
        try {
            List<EventDTO> events = eventService.getEventsByMilestoneId(milestoneId);
            if (events == null || events.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(events);
        } catch (Exception ex) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving events for milestone ID " + milestoneId + ": " + ex.getMessage());
        }
    }
    @PostMapping("/milestone/{milestoneId}")
    public ResponseEntity<?> addEventToMilestone(
            @PathVariable("milestoneId") String milestoneId,
            @RequestBody CreateEventDTO createEventDTO) {
        try {
            createEventDTO.setMilestoneID(milestoneId);
            EventDTO createdEvent = eventService.createEvent(createEventDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating event: " + ex.getMessage());
        }
    }
}
