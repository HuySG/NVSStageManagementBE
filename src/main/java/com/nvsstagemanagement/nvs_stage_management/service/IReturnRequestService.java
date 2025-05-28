package com.nvsstagemanagement.nvs_stage_management.service;


import com.nvsstagemanagement.nvs_stage_management.dto.returnAsset.ReturnRequestDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.returnAsset.ReturnRequestResponseDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.returnAsset.ProcessReturnRequestDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.returnAsset.ReturnRequestStatisticsDTO;

import java.util.List;

public interface IReturnRequestService {
    ReturnRequestResponseDTO createReturnRequest(ReturnRequestDTO dto, String staffId);
    ReturnRequestResponseDTO processReturnRequest(
            String requestId,
            ProcessReturnRequestDTO dto,
            String leaderId
    );
    List<ReturnRequestResponseDTO> getStaffRequests(String staffId);
    List<ReturnRequestResponseDTO> getPendingRequests();
    ReturnRequestResponseDTO getReturnRequestById(String requestId);
    List<ReturnRequestResponseDTO> getDepartmentRequests(String departmentId);
    List<ReturnRequestResponseDTO> getProjectRequests(String projectId);
    ReturnRequestStatisticsDTO getReturnRequestStatistics();
}