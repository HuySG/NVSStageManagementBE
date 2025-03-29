package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.request.AllocateAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.RequestAssetDTO;

import java.util.List;

public interface IRequestApprovalService {
    RequestAssetDTO allocateAssets(String requestId, List<AllocateAssetDTO> allocationDTOs);
}
