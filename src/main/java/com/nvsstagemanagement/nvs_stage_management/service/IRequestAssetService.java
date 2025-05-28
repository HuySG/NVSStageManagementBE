package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.request.AllocateAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.*;

import java.util.List;

public interface IRequestAssetService {
    List<RequestAssetDTO> getAllRequest();
    List<RequestAssetDTO> createRequest(List<CreateRequestAssetDTO> dtos);
    RequestAssetDTO getRequestById(String id);
    RequestAssetDTO updateRequestAssetStatus(UpdateRequestAssetStatusDTO dto);
    List<RequestAssetDTO> getRequestsByAssetId(String assetId);
    List<RequestAssetDTO> getRequestsByUser(String userId);
    List<RequestAssetDTO> getRequestsForAssetManager();
    List<DepartmentLeaderRequestDTO> getDepartmentLeaderRequests(String departmentId);
    List<RequestAssetDTO> createBookingRequests(CreateBookingRequestDTO dto);
    RequestAssetDTO createCategoryRequest(CreateCategoryRequestDTO dto);
    RequestAssetDTO acceptCategoryRequest(String requestId);
    RequestAssetDTO acceptBooking(String requestId, String approverId);
    List<RequestAssetDTO> getRequestByTask(String taskId);
    CheckAvailabilityResult checkAssetAvailabilityAndReturnAssets(String requestId);
    List<AssetDTO> getAllocatedAssetsByRequestId(String requestId);

}
