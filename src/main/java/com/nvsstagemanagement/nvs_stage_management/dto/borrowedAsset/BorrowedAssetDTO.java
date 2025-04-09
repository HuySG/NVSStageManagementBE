package com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BorrowedAssetDTO {
    private String borrowedID;
    private String assetID;
    private String taskID;
    private Instant borrowTime;
    private Instant endTime;
    private String description;
    private String status;
}
