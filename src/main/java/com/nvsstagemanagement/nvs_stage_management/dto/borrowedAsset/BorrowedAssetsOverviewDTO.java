package com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset;

import lombok.Data;

import java.util.List;
@Data
public class BorrowedAssetsOverviewDTO {
    private int totalBorrowedAssets;
    private List<ProjectBorrowedAssetsDTO> projects;
}
