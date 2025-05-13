package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.milestone.CreateMilestoneDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.milestone.MilestoneDTO;
import com.nvsstagemanagement.nvs_stage_management.enums.MilestoneStatus;
import com.nvsstagemanagement.nvs_stage_management.enums.ProjectStatus;
import com.nvsstagemanagement.nvs_stage_management.model.Milestone;
import com.nvsstagemanagement.nvs_stage_management.model.Project;
import com.nvsstagemanagement.nvs_stage_management.repository.MilestoneRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.ProjectRepository;
import com.nvsstagemanagement.nvs_stage_management.service.IMilestoneService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MilestoneService implements IMilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private void validateMilestoneWithinProject(Milestone milestone, Project project) {
        if (project.getStartTime() != null && milestone.getStartDate() != null
                && milestone.getStartDate().isBefore(project.getStartTime().atZone(java.time.ZoneId.systemDefault()).toLocalDate())) {
            throw new RuntimeException("Milestone start date cannot be before project start time.");
        }

        if (project.getEndTime() != null && milestone.getEndDate() != null
                && milestone.getEndDate().isAfter(project.getEndTime().atZone(java.time.ZoneId.systemDefault()).toLocalDate())) {
            throw new RuntimeException("Milestone end date cannot be after project end time.");
        }
    }
    @Override
    public MilestoneDTO createMilestone(CreateMilestoneDTO dto) {
        Project project = projectRepository.findById(dto.getProjectID())
                .orElseThrow(() -> new RuntimeException("Project not found: " + dto.getProjectID()));
        if (dto.getStartDate() == null || dto.getEndDate() == null) {
            throw new IllegalArgumentException("Milestone start date and end date must not be null.");
        }
        LocalDate projectStart = project.getStartTime() != null
                ? project.getStartTime().atZone(ZoneId.systemDefault()).toLocalDate()
                : null;
        LocalDate projectEnd = project.getEndTime() != null
                ? project.getEndTime().atZone(ZoneId.systemDefault()).toLocalDate()
                : null;
        if (projectStart != null && dto.getStartDate().isBefore(projectStart)) {
            throw new IllegalArgumentException("Milestone start date cannot be before project start date: " + projectStart);
        }
        if (projectEnd != null && dto.getEndDate().isAfter(projectEnd)) {
            throw new IllegalArgumentException("Milestone end date cannot be after project end date: " + projectEnd);
        }
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("Milestone end date cannot be before start date.");
        }
        if (project.getStatus() == ProjectStatus.NEW) {
            project.setStatus(ProjectStatus.IN_PROGRESS);
            projectRepository.save(project);
        }
        Milestone milestone = new Milestone();
        milestone.setMilestoneID(UUID.randomUUID().toString());
        milestone.setTitle(dto.getTitle());
        milestone.setDescription(dto.getDescription());
        milestone.setStartDate(dto.getStartDate());
        milestone.setEndDate(dto.getEndDate());
        milestone.setProject(project);
        milestone.setStatus(MilestoneStatus.NOT_STARTED);

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
        if (milestoneDTO.getStatus() != null) {
            existing.setStatus(milestoneDTO.getStatus());
        }
        if (milestoneDTO.getProjectID() != null && !milestoneDTO.getProjectID().trim().isEmpty()) {
            Project project = projectRepository.findById(milestoneDTO.getProjectID())
                    .orElseThrow(() -> new RuntimeException("Project not found: " + milestoneDTO.getProjectID()));
            existing.setProject(project);
        }

        if (existing.getStartDate() != null && existing.getEndDate() != null
                && existing.getStartDate().isAfter(existing.getEndDate())) {
            throw new RuntimeException("Start date must be before End date.");
        }

        validateMilestoneWithinProject(existing, existing.getProject());

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
//        if (milestone.getEvents() != null) {
//            List<EventDTO> eventDTOs = milestone.getEvents().stream()
//                    .map(event -> modelMapper.map(event, EventDTO.class))
//                    .collect(Collectors.toList());
//            dto.setEvents(eventDTOs);
//        }
        return dto;
    }
}
