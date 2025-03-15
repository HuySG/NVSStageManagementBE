package com.nvsstagemanagement.nvs_stage_management.dto.project;

import com.nvsstagemanagement.nvs_stage_management.dto.milestone.MilestoneDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskDTO;
import com.nvsstagemanagement.nvs_stage_management.model.Milestone;
import lombok.Data;

import java.util.List;
@Data
public class ProjectMilestoneDTO extends ProjectDTO {
    private List<MilestoneDTO> milestones;
}
