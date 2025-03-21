package com.nvsstagemanagement.nvs_stage_management.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateEventDTO {
    @NotBlank(message = "Event name must not be blank")
    private String eventName;
    private String description;
    @NotNull(message = "StartTime is required")
    private LocalDate startTime;
    @NotNull(message = "EndTime is required")
    private LocalDate endTime;
    private String status;
    private String image;
    private String milestoneID;
}
