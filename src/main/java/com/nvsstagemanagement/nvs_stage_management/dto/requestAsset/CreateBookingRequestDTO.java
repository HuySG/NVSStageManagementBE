package com.nvsstagemanagement.nvs_stage_management.dto.requestAsset;

import lombok.Data;

import java.time.Instant;

@Data
public class CreateBookingRequestDTO {
    private String requestId;
    private String title;
    private String description;
    private Instant startTime;
    private Instant endTime;
    private String assetID;
    private String taskID;
}
