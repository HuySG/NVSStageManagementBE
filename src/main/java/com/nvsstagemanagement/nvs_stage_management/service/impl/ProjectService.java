package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.department.DepartmentDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.milestone.MilestoneDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.project.*;
import com.nvsstagemanagement.nvs_stage_management.enums.ProjectStatus;
import com.nvsstagemanagement.nvs_stage_management.model.*;
import com.nvsstagemanagement.nvs_stage_management.repository.*;
import com.nvsstagemanagement.nvs_stage_management.service.IProjectService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService implements IProjectService {
    private final ProjectRepository projectRepository;
    private final DepartmentRepository departmentRepository;
    private final DepartmentProjectRepository departmentProjectRepository;
    private final ProjectTypeRepository projectTypeRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    @Override
    public List<ProjectDepartmentDTO> getAllProject() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream()
                .map(project -> modelMapper.map(project, ProjectDepartmentDTO.class)).toList();
    }

    @Override
    public ProjectDTO createProject(CreateProjectDTO createProjectDTO) {
        Project project = new Project();
        project.setProjectID(UUID.randomUUID().toString());
        project.setTitle(createProjectDTO.getTitle());
        project.setDescription(createProjectDTO.getDescription());
        project.setContent(createProjectDTO.getContent());
        project.setStartTime(createProjectDTO.getStartTime());
        project.setEndTime(createProjectDTO.getEndTime());
        project.setCreatedBy(createProjectDTO.getCreatedBy());
        project.setStatus(ProjectStatus.NEW);
        ProjectType projectType = projectTypeRepository.findById(createProjectDTO.getProjectTypeID())
                .orElseThrow(() -> new RuntimeException("ProjectTypeDTO not found: " + createProjectDTO.getProjectTypeID()));
        project.setProjectType(projectType);

        Project savedProject = projectRepository.save(project);
        return modelMapper.map(savedProject, ProjectDTO.class);

    }

    @Override
    public List<DepartmentProjectDTO> assignDepartmentToProject(String projectID,DepartmentProjectDTO dto) {

        Project project = projectRepository.findById(projectID)
                .orElseThrow(() -> new RuntimeException("Project not found: " + projectID));

        List<DepartmentProjectDTO> resultList = new ArrayList<>();

        for (String deptID : dto.getDepartmentID()) {
            Department department = departmentRepository.findById(deptID)
                    .orElseThrow(() -> new RuntimeException("Department not found: " + deptID));

            DepartmentProjectId dpId = new DepartmentProjectId(deptID, projectID);

            if (departmentProjectRepository.existsById(dpId)) {
                throw new RuntimeException("Department " + deptID
                        + " is already assigned to project " + projectID);
            }


            DepartmentProject departmentProject = new DepartmentProject();
            departmentProject.setId(dpId);
            departmentProject.setDepartment(department);
            departmentProject.setProject(project);
            departmentProjectRepository.save(departmentProject);

            DepartmentProjectDTO responseDTO = new DepartmentProjectDTO();
            responseDTO.setDepartmentID(Collections.singletonList(deptID));
            resultList.add(responseDTO);
        }

        return resultList;
    }

    @Override
    public List<ProjectMilestoneDepartmentDTO> getAllProjectWithMilestone() {
        List<Project> projects = projectRepository.findAllWithMilestonesAndTasks();
        return projects.stream()
                .map(project -> {
                    ProjectMilestoneDepartmentDTO dto = modelMapper.map(project, ProjectMilestoneDepartmentDTO.class);
                    return dto;
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<ProjectDepartmentDTO> getProjectWithUserId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        List<Project> projects = projectRepository.findShowByUserId(userId);

        return projects.stream().map(project -> {
            ProjectDepartmentDTO dto = modelMapper.map(project, ProjectDepartmentDTO.class);
            return dto;
        }).collect(Collectors.toList());
    }
    public List<ProjectDepartmentDTO> getProjectsByDepartmentId(String departmentId) {
        List<Project> projects = departmentProjectRepository.findProjectsByDepartmentId(departmentId);
        return projects.stream()
                .map(project -> modelMapper.map(project, ProjectDepartmentDTO.class))
                .collect(Collectors.toList());
    }
    @Override
    public ProjectMilestoneDepartmentDTO getProjectWithMilestones(String projectId) {

        Project project = projectRepository.findProjectWithMilestonesAndDepartmentsById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found: " + projectId));

        ProjectMilestoneDepartmentDTO dto = modelMapper.map(project, ProjectMilestoneDepartmentDTO.class);

        if (project.getDepartmentProjects() != null && !project.getDepartmentProjects().isEmpty()) {
            List<DepartmentDTO> departmentDTOList = project.getDepartmentProjects().stream()
                    .map(departmentProject -> {
                        Department department = departmentProject.getDepartment();
                        return modelMapper.map(department, DepartmentDTO.class);
                    })
                    .collect(Collectors.toList());
            dto.setDepartments(departmentDTOList);
        } else {
            dto.setDepartments(new ArrayList<>());
        }
        if (project.getMilestones() != null && !project.getMilestones().isEmpty()) {
            List<MilestoneDTO> milestoneDTOList = project.getMilestones().stream()
                    .map(milestone -> modelMapper.map(milestone, MilestoneDTO.class))
                    .collect(Collectors.toList());
            dto.setMilestones(milestoneDTOList);
        } else {
            dto.setMilestones(new ArrayList<>());
        }

        return dto;

    }
    @Override
    public ProjectMilestoneDepartmentDTO getProjectByMilestoneId(String milestoneId) {
        Project project = projectRepository.findProjectByMilestoneId(milestoneId)
                .orElseThrow(() -> new RuntimeException("Project not found with milestone ID: " + milestoneId));

        ProjectMilestoneDepartmentDTO dto = modelMapper.map(project, ProjectMilestoneDepartmentDTO.class);

        if (project.getDepartmentProjects() != null && !project.getDepartmentProjects().isEmpty()) {
            List<DepartmentDTO> departmentDTOList = project.getDepartmentProjects().stream()
                    .map(departmentProject -> modelMapper.map(departmentProject.getDepartment(), DepartmentDTO.class))
                    .collect(Collectors.toList());
            dto.setDepartments(departmentDTOList);
        } else {
            dto.setDepartments(new ArrayList<>());
        }

        if (project.getMilestones() != null && !project.getMilestones().isEmpty()) {
            List<MilestoneDTO> milestoneDTOList = project.getMilestones().stream()
                    .map(milestone -> modelMapper.map(milestone, MilestoneDTO.class))
                    .collect(Collectors.toList());
            dto.setMilestones(milestoneDTOList);
        } else {
            dto.setMilestones(new ArrayList<>());
        }

        return dto;
    }
    @Override
    public ProjectDTO updateProject(String projectId, UpdateProjectDTO updateProjectDTO) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        boolean statusChanged = false;

        if (updateProjectDTO.getTitle() != null) {
            project.setTitle(updateProjectDTO.getTitle());
        }
        if (updateProjectDTO.getDescription() != null) {
            project.setDescription(updateProjectDTO.getDescription());
        }
        if (updateProjectDTO.getContent() != null) {
            project.setContent(updateProjectDTO.getContent());
        }
        if (updateProjectDTO.getStartTime() != null) {
            project.setStartTime(updateProjectDTO.getStartTime());
        }
        if (updateProjectDTO.getEndTime() != null) {
            project.setEndTime(updateProjectDTO.getEndTime());
        }
        if (updateProjectDTO.getProjectTypeID() != null) {
            ProjectType projectType = projectTypeRepository.findById(updateProjectDTO.getProjectTypeID())
                    .orElseThrow(() -> new RuntimeException("ProjectType not found: " + updateProjectDTO.getProjectTypeID()));
            project.setProjectType(projectType);
        }
        if (updateProjectDTO.getStatus() != null) {
            project.setStatus(updateProjectDTO.getStatus());
            statusChanged = true;
        }

        if (project.getStartTime() != null && project.getEndTime() != null
                && project.getStartTime().isAfter(project.getEndTime())) {
            throw new RuntimeException("Start time must be before End time.");
        }

        if (project.getEndTime() != null && project.getEndTime().isBefore(Instant.now())
                && (project.getStatus() == ProjectStatus.NEW || project.getStatus() == ProjectStatus.IN_PROGRESS)) {
            throw new RuntimeException("Cannot set end time in the past for a project that is not completed.");
        }

        if (!statusChanged && project.getEndTime() != null && Instant.now().isAfter(project.getEndTime())) {
            if (project.getStatus() == ProjectStatus.IN_PROGRESS) {
                project.setStatus(ProjectStatus.COMPLETED);
            }
        }
        Project updatedProject = projectRepository.save(project);
        return modelMapper.map(updatedProject, ProjectDTO.class);
    }



}
