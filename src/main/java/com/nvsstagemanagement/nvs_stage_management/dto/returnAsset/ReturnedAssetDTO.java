package com.nvsstagemanagement.nvs_stage_management.dto.returnAsset;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class ReturnedAssetDTO {
    private String returnedAssetID;
    private String taskID;
    private String assetID;
    private Instant returnTime;
    private String description;
    private String milestoneName;
    private String projectID;
    private String title;
    private BigDecimal latePenaltyFee;
    private String conditionAfter;
    private String imageAfter;
}
