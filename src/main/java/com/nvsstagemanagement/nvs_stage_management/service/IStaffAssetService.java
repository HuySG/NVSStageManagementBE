package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.StaffAllocatedAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.StaffBorrowedAssetDTO;

import java.util.List;

public interface IStaffAssetService {
    List<StaffAllocatedAssetDTO> getAllocatedAssetsByStaff(String staffId);
}
