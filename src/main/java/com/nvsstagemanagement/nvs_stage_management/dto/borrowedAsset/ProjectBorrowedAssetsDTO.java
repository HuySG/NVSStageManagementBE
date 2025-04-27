package com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset;

import lombok.Data;

@Data
public class ProjectBorrowedAssetsDTO {
    private String projectId;
    private String projectTitle;
    private int departmentsUsing;
    private int borrowedAssetsCount;
}