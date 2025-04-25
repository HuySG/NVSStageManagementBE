package com.nvsstagemanagement.nvs_stage_management.dto.requestAsset;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckAvailabilityResult {

    private boolean isAvailable;
    private String message;

    private List<AssetDTO> availableAssets = new ArrayList<>();

    private List<MissingCategoryInfo> missingCategories = new ArrayList<>();

    public void addMissingCategory(String categoryID, String categoryName,
                                   int requestedQuantity, int availableNow,
                                   Instant nextAvailableTime) {
        MissingCategoryInfo dto = new MissingCategoryInfo();
        dto.setCategoryId(categoryID);
        dto.setCategoryName(categoryName);
        dto.setRequestedQuantity(requestedQuantity);
        dto.setAvailableNow(availableNow);
        dto.setShortage(requestedQuantity - availableNow);
        dto.setNextAvailableTime(nextAvailableTime);
        missingCategories.add(dto);
    }
    public void addAvailableAsset(AssetDTO assetDTO) {
        this.availableAssets.add(assetDTO);
    }
}
