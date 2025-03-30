package com.nvsstagemanagement.nvs_stage_management.dto.requestAsset;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class CreateCategoryRequestDTO {
    private String title;
    private String description;
    private Instant startTime;
    private Instant endTime;
    private String taskID;
    private List<CreateCategoryRequestItemDTO> categories;
}
