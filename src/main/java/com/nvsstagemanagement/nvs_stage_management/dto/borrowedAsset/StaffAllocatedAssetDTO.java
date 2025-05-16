package com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset;

import com.nvsstagemanagement.nvs_stage_management.enums.AllocationStatus;
import lombok.Data;

import java.time.Instant;

@Data
public class StaffAllocatedAssetDTO {
    private String allocationId;
    private String assetId;
    private String assetName;
    private String categoryId;
    private String categoryName;
    private AllocationStatus status;
    private Instant requestTime;
    private Instant startTime;
    private Instant endTime;
}
