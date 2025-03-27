package com.nvsstagemanagement.nvs_stage_management.dto.event;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EventDTO {
    private String eventID;
    private String eventName;
    private String description;
    private LocalDate startTime;
    private LocalDate endTime;
    private String status;
    private String image;
    private String milestoneID;
}
