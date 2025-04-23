package com.nvsstagemanagement.nvs_stage_management.dto.allocation;

import lombok.Data;

import java.time.Instant;
@Data
public class AllocatedAssetDTO {
    private String assetID;
    private String assetName;
    private String category;
    private String status;
    private Instant startTime;
    private Instant endTime;
}
