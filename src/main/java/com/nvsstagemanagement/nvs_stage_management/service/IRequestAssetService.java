package com.nvsstagemanagement.nvs_stage_management.service;

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

    RequestAssetDTO acceptRequest(String requestId);

    RequestAssetDTO createBookingRequest(CreateBookingRequestDTO dto);

    RequestAssetDTO createCategoryRequest(CreateCategoryRequestDTO dto);

    RequestAssetDTO acceptCategoryRequest(String requestId, ApprovalDTO approvalDTO);

    RequestAssetDTO acceptBooking(String requestId, String approverId);
}
