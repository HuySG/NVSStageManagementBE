package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.event.CreateEventDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.event.EventDTO;
import com.nvsstagemanagement.nvs_stage_management.model.Event;
import com.nvsstagemanagement.nvs_stage_management.model.Milestone;
import com.nvsstagemanagement.nvs_stage_management.repository.EventRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.MilestoneRepository;
import com.nvsstagemanagement.nvs_stage_management.service.IEventService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService implements IEventService {
    private final EventRepository eventRepository;
    private final MilestoneRepository milestoneRepository;
    private final ModelMapper modelMapper;
    @Override
    public EventDTO createEvent(CreateEventDTO createEventDTO) {
        Event event = new Event();
        event.setEventID(UUID.randomUUID().toString());
        event.setEventName(createEventDTO.getEventName());
        event.setDescription(createEventDTO.getDescription());
        event.setStartTime(createEventDTO.getStartTime());
        event.setEndTime(createEventDTO.getEndTime());
        event.setStatus(createEventDTO.getStatus());
        event.setImage(createEventDTO.getImage());

        if (createEventDTO.getMilestoneID() != null && !createEventDTO.getMilestoneID().isEmpty()) {
            Milestone milestone = milestoneRepository.findById(createEventDTO.getMilestoneID())
                    .orElseThrow(() -> new RuntimeException("Milestone not found: " + createEventDTO.getMilestoneID()));
            event.setMilestone(milestone);
        }

        Event savedEvent = eventRepository.save(event);

        return mapToDTO(savedEvent);
    }

    @Override
    public EventDTO getEventById(String eventID) {
        Event event = eventRepository.findById(eventID)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventID));
        return mapToDTO(event);
    }

    @Override
    public EventDTO updateEvent(String eventID, CreateEventDTO createEventDTO) {

        Event existingEvent = eventRepository.findById(eventID)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventID));

        existingEvent.setEventName(createEventDTO.getEventName());
        existingEvent.setDescription(createEventDTO.getDescription());
        existingEvent.setStartTime(createEventDTO.getStartTime());
        existingEvent.setEndTime(createEventDTO.getEndTime());
        existingEvent.setStatus(createEventDTO.getStatus());
        existingEvent.setImage(createEventDTO.getImage());

        if (createEventDTO.getMilestoneID() != null && !createEventDTO.getMilestoneID().isEmpty()) {
            Milestone milestone = milestoneRepository.findById(createEventDTO.getMilestoneID())
                    .orElseThrow(() -> new RuntimeException("Milestone not found: " + createEventDTO.getMilestoneID()));
            existingEvent.setMilestone(milestone);
        } else {
            existingEvent.setMilestone(null);
        }

        Event updated = eventRepository.save(existingEvent);
        return mapToDTO(updated);
    }

    @Override
    public void deleteEvent(String eventID) {
        Event existingEvent = eventRepository.findById(eventID)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventID));
        eventRepository.delete(existingEvent);
    }

    @Override
    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private EventDTO mapToDTO(Event event) {
        EventDTO dto = modelMapper.map(event, EventDTO.class);
        if (event.getMilestone() != null) {
            dto.setMilestoneID(event.getMilestone().getMilestoneID());
        }
        return dto;
    }
}
