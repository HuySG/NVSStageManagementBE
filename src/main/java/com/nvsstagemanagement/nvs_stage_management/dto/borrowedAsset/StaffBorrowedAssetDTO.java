package com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset;

import com.nvsstagemanagement.nvs_stage_management.enums.AllocationStatus;
import lombok.Data;

import java.time.Instant;

@Data
public class StaffBorrowedAssetDTO {
    private String borrowedID;
    private String assetId;
    private String assetName;
    private Instant borrowTime;
    private Instant startTime;
    private Instant endTime;
    private String status;
    private String taskId;
    private String taskTitle;
    private String projectId;
}
