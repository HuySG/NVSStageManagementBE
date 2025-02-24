package com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BorrowedAssetDTO {
    private String assetID;
    private String taskID;
    private LocalDateTime borrowTime;
    private int quantity;
    private String description;
}
