package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.StaffAllocatedAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.StaffBorrowedAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.enums.BookingType;
import com.nvsstagemanagement.nvs_stage_management.enums.BorrowedAssetStatus;
import com.nvsstagemanagement.nvs_stage_management.model.BorrowedAsset;
import com.nvsstagemanagement.nvs_stage_management.model.RequestAsset;
import com.nvsstagemanagement.nvs_stage_management.model.RequestAssetAllocation;
import com.nvsstagemanagement.nvs_stage_management.repository.BorrowedAssetRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.RequestAssetAllocationRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.RequestAssetRepository;
import com.nvsstagemanagement.nvs_stage_management.service.IStaffAssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffAssetService implements IStaffAssetService {
    private final BorrowedAssetRepository borrowedAssetRepository;
    private final RequestAssetAllocationRepository requestAssetAllocationRepository;
    private final RequestAssetRepository requestAssetRepository;
    @Override
    public List<StaffBorrowedAssetDTO> getBorrowedAssetsByStaff(String staffId) {
        return borrowedAssetRepository
                .findByTask_AssigneeAndStatus(staffId, BorrowedAssetStatus.IN_USE.name())
                .stream()
                .map(this::toBorrowedDto)
                .collect(Collectors.toList());
    }

    private StaffBorrowedAssetDTO toBorrowedDto(BorrowedAsset ba) {
        StaffBorrowedAssetDTO dto = new StaffBorrowedAssetDTO();
        dto.setBorrowedID(ba.getBorrowedID());
        dto.setAssetId(ba.getAsset().getAssetID());
        dto.setAssetName(ba.getAsset().getAssetName());
        dto.setBorrowTime(ba.getBorrowTime());
        dto.setStartTime(ba.getStartTime());
        dto.setEndTime(ba.getEndTime());
        dto.setStatus(ba.getStatus());
        dto.setTaskId(ba.getTask().getTaskID());
        dto.setTaskTitle(ba.getTask().getTitle());
        return dto;
    }

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





