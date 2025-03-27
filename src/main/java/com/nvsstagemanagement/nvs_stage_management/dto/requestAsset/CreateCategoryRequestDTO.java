package com.nvsstagemanagement.nvs_stage_management.dto.requestAsset;

import lombok.Data;

import java.time.Instant;

@Data
public class CreateCategoryRequestDTO {
    private String requestId;
    private String title;
    private String description;
    private Instant startTime;
    private Instant endTime;
    private String categoryID;
    private Integer quantity;
    private String taskID;
}
