package com.nvsstagemanagement.nvs_stage_management.dto.requestAsset;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MissingCategoryInfo {

    private String categoryId;
    private String categoryName;

    private int requestedQuantity;
    private int availableNow;
    private int shortage;

    private Instant nextAvailableTime;
}

