package com.nvsstagemanagement.nvs_stage_management.dto.requestAsset;

import lombok.Data;

@Data
public class UpdateRequestAssetStatusDTO {
    private String requestId;
    private String status;
}
