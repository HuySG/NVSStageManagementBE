package com.nvsstagemanagement.nvs_stage_management.dto.project;

import com.nvsstagemanagement.nvs_stage_management.dto.department.DepartmentDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.milestone.MilestoneDTO;
import lombok.Data;

import java.util.List;
@Data
public class ProjectMilestoneDepartmentDTO extends ProjectDepartmentDTO {
    private List<MilestoneDTO> milestones;
    private List<DepartmentDTO> departments;
}
