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

    private int requestedQuantity;    // Tổng số lượng yêu cầu
    private int availableNow;         // Số lượng hiện đang có sẵn
    private int shortage;       // Số lượng thiếu = requested - available

    private Instant nextAvailableTime;
}

