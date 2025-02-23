package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.event.EventDTO;

import java.util.List;

public interface IEventService {
    List<EventDTO> getEventsByProjectID(String projectId);
    EventDTO createEvent(EventDTO eventDTO);
}
