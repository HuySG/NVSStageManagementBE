package com.nvsstagemanagement.nvs_stage_management.dto.requestAsset;

import lombok.Data;

import java.time.Instant;

@Data
public class CreateRequestAssetDTO {
    private String requestId;
    private Integer quantity;
    private String discription;
    private Instant startTime;
    private Instant endTime;
    private String assetID;
    private String taskID;
}
