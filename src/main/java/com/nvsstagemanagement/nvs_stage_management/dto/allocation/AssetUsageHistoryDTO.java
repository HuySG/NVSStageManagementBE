package com.nvsstagemanagement.nvs_stage_management.dto.allocation;

import lombok.Data;

import java.time.Instant;

@Data
public class AssetUsageHistoryDTO {
    private String usageID;
    private Instant startDate;
    private Instant endDate;
    private String status;
    private String projectID;
    private String projectName;
}
