package com.nvsstagemanagement.nvs_stage_management.dto.requestAsset;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCategoryRequestItemDTO {
    private String categoryID;
    private Integer quantity;
}
