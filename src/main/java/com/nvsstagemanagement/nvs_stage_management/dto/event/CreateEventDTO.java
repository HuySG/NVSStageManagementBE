//package com.nvsstagemanagement.nvs_stage_management.dto.event;
//
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import lombok.Data;
//
//import java.time.LocalDateTime;
//
//@Data
//public class CreateEventDTO {
//    @NotBlank(message = "Event name is required")
//    private String eventName;
//    private String description;
//    private String eventType;
//    private Integer locationID;
//    @NotNull(message = "Start time is required")
//    private LocalDateTime startTime;
//    @NotNull(message = "End time is required")
//    private LocalDateTime endTime;
//    private String status;
//    private String image;
//    private String createdByID;
//    private LocalDateTime createdDate;
//    private String milestoneID;
//}
