package com.nvsstagemanagement.nvs_stage_management.dto.requestAsset;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class AssetPreparationDTO {
    private String allocationId;
    private String assetId;
    private String assetName;
    private String categoryId;
    private String categoryName;
    private String requestId;
    private String requestTitle;
    private Instant startTime;
    private Instant endTime;
    private String status;
    private String conditionBefore;
    private List<String> imageUrls;
}
