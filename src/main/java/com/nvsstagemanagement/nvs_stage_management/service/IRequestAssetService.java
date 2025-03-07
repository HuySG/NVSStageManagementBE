package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.CreateRequestAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.RequestAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.UpdateRequestAssetStatusDTO;

import java.util.List;

public interface IRequestAssetService {
    List<RequestAssetDTO> getAllRequest ();
    RequestAssetDTO createRequest(CreateRequestAssetDTO createRequestAssetDTO);
    RequestAssetDTO getRequestById(String id);
    RequestAssetDTO updateRequestAssetStatus(UpdateRequestAssetStatusDTO dto);
    List<RequestAssetDTO> getRequestsForLeader(String departmentId, String status);
    List<RequestAssetDTO> getRequestsByUser(String userId);
    List<RequestAssetDTO> getRequestsForAssetManager();
}
