package com.nvsstagemanagement.nvs_stage_management.dto.requestAsset;

import com.nvsstagemanagement.nvs_stage_management.dto.user.UserDTO;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class CreateRequestAssetDTO {
    private Integer quantity;
    private String description;
    @NotBlank(message = "Title is required")
    private String title;
    @NotNull(message = "Start time is required")
    private Instant startTime;
    @NotNull(message = "End time is required")
    private Instant endTime;
    private String assetID;
    private String categoryID;
    private String taskID;
    @AssertTrue(message = "Either assetID or categoryID must be provided")
    public boolean isAssetOrCategoryProvided() {
        return (assetID != null && !assetID.trim().isEmpty())
                || (categoryID != null && !categoryID.trim().isEmpty());
    }
    @AssertTrue(message = "If categoryID is provided, quantity must be provided and greater than 0")
    public boolean isQuantityValid() {
        if (categoryID != null && !categoryID.trim().isEmpty()) {
            return quantity != null && quantity > 0;
        }
        return true;
    }
}
