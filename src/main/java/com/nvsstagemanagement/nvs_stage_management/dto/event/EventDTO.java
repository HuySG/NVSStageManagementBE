package com.nvsstagemanagement.nvs_stage_management.dto.event;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
@Data
public class EventDTO {
    private String eventID;
    private String eventName;
    private String description;
    private Instant startTime;
    private Instant endTime;
    private String location;
    private String createdBy;
    private String status;
    private String image;
    private String projectID;
}
