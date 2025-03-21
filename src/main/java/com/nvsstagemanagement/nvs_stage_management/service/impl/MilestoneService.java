package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.event.EventDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.milestone.MilestoneDTO;
import com.nvsstagemanagement.nvs_stage_management.model.Milestone;
import com.nvsstagemanagement.nvs_stage_management.model.Project;
import com.nvsstagemanagement.nvs_stage_management.repository.MilestoneRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.ProjectRepository;
import com.nvsstagemanagement.nvs_stage_management.service.IMilestoneService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MilestoneService implements IMilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;

    @Override
    public MilestoneDTO createMilestone(MilestoneDTO milestoneDTO) {
        Milestone milestone = modelMapper.map(milestoneDTO, Milestone.class);
        if (milestone.getMilestoneID() == null || milestone.getMilestoneID().trim().isEmpty()) {
            milestone.setMilestoneID(UUID.randomUUID().toString());
        }
        Project project = projectRepository.findById(milestoneDTO.getProjectID())
                .orElseThrow(() -> new RuntimeException("Project not found: " + milestoneDTO.getProjectID()));
        milestone.setProject(project);
        Milestone savedMilestone = milestoneRepository.save(milestone);
        return modelMapper.map(savedMilestone, MilestoneDTO.class);
    }

    @Override
    public MilestoneDTO getMilestone(String milestoneID) {
        Milestone milestone = milestoneRepository.findById(milestoneID)
                .orElseThrow(() -> new RuntimeException("Milestone not found: " + milestoneID));
        return modelMapper.map(milestone, MilestoneDTO.class);
    }

    @Override
    public MilestoneDTO updateMilestone(String milestoneID, MilestoneDTO milestoneDTO) {
        Milestone existing = milestoneRepository.findById(milestoneID)
                .orElseThrow(() -> new RuntimeException("Milestone not found: " + milestoneID));
        if (milestoneDTO.getTitle() != null && !milestoneDTO.getTitle().trim().isEmpty()) {
            existing.setTitle(milestoneDTO.getTitle());
        }
        if (milestoneDTO.getDescription() != null) {
            existing.setDescription(milestoneDTO.getDescription());
        }
        if (milestoneDTO.getStartDate() != null) {
            existing.setStartDate(milestoneDTO.getStartDate());
        }
        if (milestoneDTO.getEndDate() != null) {
            existing.setEndDate(milestoneDTO.getEndDate());
        }
        if (milestoneDTO.getProjectID() != null && !milestoneDTO.getProjectID().trim().isEmpty()) {
            Project project = projectRepository.findById(milestoneDTO.getProjectID())
                    .orElseThrow(() -> new RuntimeException("Project not found: " + milestoneDTO.getProjectID()));
            existing.setProject(project);
        }
        Milestone savedMilestone = milestoneRepository.save(existing);
        return modelMapper.map(savedMilestone, MilestoneDTO.class);
    }

    @Override
    public void deleteMilestone(String milestoneID) {
        if (!milestoneRepository.existsById(milestoneID)) {
            throw new RuntimeException("Milestone not found: " + milestoneID);
        }
        milestoneRepository.deleteById(milestoneID);
    }

    @Override
    public List<MilestoneDTO> getMilestonesByProject(String projectID) {
        List<Milestone> milestones = milestoneRepository.findMilestonesWithEventsByProjectID(projectID);
        return milestones.stream()
                .map(this::mapMilestoneToDTO)
                .collect(Collectors.toList());
    }
    private MilestoneDTO mapMilestoneToDTO(Milestone milestone) {
        MilestoneDTO dto = modelMapper.map(milestone, MilestoneDTO.class);
        if (milestone.getEvents() != null) {
            List<EventDTO> eventDTOs = milestone.getEvents().stream()
                    .map(event -> modelMapper.map(event, EventDTO.class))
                    .collect(Collectors.toList());
            dto.setEvents(eventDTOs);
        }
        return dto;
    }
}
