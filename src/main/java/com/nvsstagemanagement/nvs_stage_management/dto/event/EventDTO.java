package com.nvsstagemanagement.nvs_stage_management.dto.event;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EventDTO {
    private String eventID;
    private String eventName;
    private String description;
    private String eventType;
    private String locationID;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String image;
    private String createdByID;
    private LocalDateTime createdDate;
    private String milestoneID;
}
