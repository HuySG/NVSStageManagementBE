package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.event.EventDTO;
import com.nvsstagemanagement.nvs_stage_management.model.Event;
import com.nvsstagemanagement.nvs_stage_management.repository.EventRepository;
import com.nvsstagemanagement.nvs_stage_management.service.IEventService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class EventService implements IEventService {
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    public List<EventDTO> getEventsByProjectID(String projectId) {
        List<Event> events = eventRepository.findByProject_ProjectID(projectId);
        return events.stream()
                .map(event -> modelMapper.map(event, EventDTO.class)).toList();
    }

    @Override
    public EventDTO createEvent(EventDTO eventDTO) {
        Event createdEvent = modelMapper.map(eventDTO, Event.class);
        Event savedEvent = eventRepository.save(createdEvent);
        return modelMapper.map(savedEvent,EventDTO.class);
    }
}
