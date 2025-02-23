package com.nvsstagemanagement.nvs_stage_management.dto.asset;

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
    private String assetName;
    private String model;
    private String code;
    private String description;
    private BigDecimal price;
    private LocalDate buyDate;
    private String status;
    private String location;
    private String createdBy;
    private Integer quantity;
    private String image;
    private String categoryID;
    private String assetTypeID;
}
