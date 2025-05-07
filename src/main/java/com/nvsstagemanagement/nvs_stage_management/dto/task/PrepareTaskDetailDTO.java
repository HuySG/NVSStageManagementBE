package com.nvsstagemanagement.nvs_stage_management.dto.task;

import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.AssetPreparationDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.RequestAssetDTO;
import lombok.Data;

import java.util.List;

@Data
public class PrepareTaskDetailDTO {
    private TaskDTO prepareTask;
    private TaskDTO requestTask;
    private RequestAssetDTO request;
    private List<AssetPreparationDTO> assets;
}
