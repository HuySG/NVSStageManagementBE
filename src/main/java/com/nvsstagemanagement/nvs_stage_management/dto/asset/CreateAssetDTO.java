package com.nvsstagemanagement.nvs_stage_management.dto.asset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAssetDTO {
    private String assetID;
    @NotBlank(message = "Asset name is required.")
    @Size(max = 50, message = "Asset name must not exceed 50 characters.")
    private String assetName;
    @Size(max = 50, message = "Model must not exceed 255 characters.")
    private String model;
    @Size(max = 50, message = "Code must not exceed 50 characters.")
    private String code;
    @Size(max = 1000, message = "Description must not exceed 1000 characters.")
    private String description;
    private BigDecimal price;
    private LocalDate buyDate;
    private String status;
    private String locationId;
    private String createdBy;
    private String image;
    @NotBlank(message = "Category ID is required.")
    private String categoryID;
    @NotBlank(message = "Asset type ID is required.")
    private String assetTypeID;
}
