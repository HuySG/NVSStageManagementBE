package com.nvsstagemanagement.nvs_stage_management.dto.requestAsset;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckAvailabilityResult {
    private boolean available = false;
    private String message;
    private List<AssetDTO> availableAssets = new ArrayList<>();
    private Map<String, Integer> missingCategories = new HashMap<>();
    public void addAvailableAsset(AssetDTO assetDTO) {
        this.availableAssets.add(assetDTO);
    }

    public void addMissingCategory(String categoryName, int missingQuantity) {
        this.missingCategories.put(categoryName, missingQuantity);
    }
}
