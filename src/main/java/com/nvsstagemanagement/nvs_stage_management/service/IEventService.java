package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.event.CreateEventDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.event.EventDTO;

import java.util.List;

public interface IEventService {
    EventDTO createEvent(CreateEventDTO createEventDTO);
    EventDTO getEventById(String eventID);
    EventDTO updateEvent(String eventID, CreateEventDTO createEventDTO);
    void deleteEvent(String eventID);
    List<EventDTO> getAllEvents();
}
