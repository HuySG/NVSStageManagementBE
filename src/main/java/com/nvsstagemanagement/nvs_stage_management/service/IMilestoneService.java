package com.nvsstagemanagement.nvs_stage_management.service;



import com.nvsstagemanagement.nvs_stage_management.dto.milestone.MilestoneDTO;

import java.util.List;

public interface IMilestoneService {
    MilestoneDTO createMilestone(MilestoneDTO milestoneDTO);
    MilestoneDTO getMilestone(String milestoneID);
    MilestoneDTO updateMilestone(String milestoneID, MilestoneDTO milestoneDTO);
    void deleteMilestone(String milestoneID);
    List<MilestoneDTO> getMilestonesByProject(String projectID);
}
