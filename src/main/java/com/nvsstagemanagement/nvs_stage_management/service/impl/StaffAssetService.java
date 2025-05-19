package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.StaffAllocatedAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.StaffBorrowedAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.enums.BookingType;
import com.nvsstagemanagement.nvs_stage_management.enums.BorrowedAssetStatus;
import com.nvsstagemanagement.nvs_stage_management.model.BorrowedAsset;
import com.nvsstagemanagement.nvs_stage_management.model.RequestAsset;
import com.nvsstagemanagement.nvs_stage_management.model.RequestAssetAllocation;
import com.nvsstagemanagement.nvs_stage_management.repository.RequestAssetAllocationRepository;
import com.nvsstagemanagement.nvs_stage_management.service.IStaffAssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffAssetService implements IStaffAssetService {

    private final RequestAssetAllocationRepository requestAssetAllocationRepository;



    @Override
    public List<StaffAllocatedAssetDTO> getAllocatedAssetsByStaff(String staffId) {
        return requestAssetAllocationRepository
                .findByRequestAsset_CreateBy(staffId)
                .stream()
                .map(this::toAllocatedDto)
                .collect(Collectors.toList());
    }

    private StaffAllocatedAssetDTO toAllocatedDto(RequestAssetAllocation ra) {
        StaffAllocatedAssetDTO dto = new StaffAllocatedAssetDTO();
        dto.setAllocationId(ra.getAllocationId());
        dto.setAssetId(ra.getAsset().getAssetID());
        dto.setAssetName(ra.getAsset().getAssetName());
        dto.setCategoryId(ra.getCategory().getCategoryID());
        dto.setCategoryName(ra.getCategory().getName());
        dto.setStatus(ra.getStatus());
        RequestAsset req = ra.getRequestAsset();
        dto.setRequestTime(req.getRequestTime());
        dto.setStartTime(req.getStartTime());
        dto.setEndTime(req.getEndTime());
        return dto;
    }

}





