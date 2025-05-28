package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.department.DepartmentDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.milestone.MilestoneDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.project.*;
import com.nvsstagemanagement.nvs_stage_management.dto.user.UserDTO;
import com.nvsstagemanagement.nvs_stage_management.enums.MilestoneStatus;
import com.nvsstagemanagement.nvs_stage_management.enums.ProjectStatus;
import com.nvsstagemanagement.nvs_stage_management.enums.RequestAssetStatus;
import com.nvsstagemanagement.nvs_stage_management.enums.TaskEnum;
import com.nvsstagemanagement.nvs_stage_management.model.*;
import com.nvsstagemanagement.nvs_stage_management.repository.*;
import com.nvsstagemanagement.nvs_stage_management.service.IProjectService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final TaskRepository taskRepository;
    private final  RequestAssetRepository requestAssetRepository;
    private final MilestoneRepository milestoneRepository ;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    @Override
    public List<ProjectDTO> getAllProject() {
        return projectRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    private ProjectDTO toDto(Project project) {
        ProjectDTO dto = modelMapper.map(project, ProjectDTO.class);
        ProjectType type = project.getProjectType();
        if (type != null) {
            dto.setProjectTypeID(type.getProjectTypeID());
            dto.setProjectTypeName(type.getTypeName());
        }
        String creatorId = project.getCreatedBy();
        userRepository.findById(creatorId).ifPresent(user -> {
            UserDTO u = modelMapper.map(user, UserDTO.class);
            dto.setCreatedByInfo(u);
        });

        return dto;
    }

    @Override
    public ProjectDTO createProject(CreateProjectDTO dto) {
        if (dto.getStartTime() != null && dto.getEndTime() != null
                && dto.getEndTime().isBefore(dto.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time.");
        }
        ProjectType type = projectTypeRepository.findById(dto.getProjectTypeID())
                .orElseThrow(() -> new RuntimeException("ProjectType not found: " + dto.getProjectTypeID()));
        Project project = new Project();
        project.setProjectID(UUID.randomUUID().toString());
        project.setTitle(dto.getTitle());
        project.setDescription(dto.getDescription());
        project.setContent(dto.getContent());
        project.setStartTime(dto.getStartTime());
        project.setEndTime(dto.getEndTime());
        project.setCreatedBy(dto.getCreatedBy());
        project.setStatus(ProjectStatus.NEW);
        project.setProjectType(type);
        Project saved = projectRepository.saveAndFlush(project);
        List<DepartmentProject> links = new ArrayList<>();
        for (String deptId : dto.getDepartments()) {
            Department dept = departmentRepository.findById(deptId)
                    .orElseThrow(() -> new RuntimeException("Department not found: " + deptId));

            DepartmentProject dp = new DepartmentProject();
            dp.setId(new DepartmentProjectId(saved.getProjectID(), deptId));
            dp.setProject(saved);
            dp.setDepartment(dept);
            links.add(dp);
        }
        departmentProjectRepository.saveAll(links);
        ProjectDTO result = modelMapper.map(saved, ProjectDTO.class);
        result.setProjectTypeID(type.getProjectTypeID());
        result.setProjectTypeName(type.getTypeName());
        result.setStatus(saved.getStatus());
        userRepository.findById(saved.getCreatedBy())
                .ifPresent(u -> result.setCreatedByInfo(modelMapper.map(u, UserDTO.class)));
        List<DepartmentDTO> deptDTOs = links.stream()
                .map(link -> modelMapper.map(link.getDepartment(), DepartmentDTO.class))
                .collect(Collectors.toList());
        result.setDepartments(deptDTOs);

        return result;
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
        return projectRepository.findAllWithMilestonesAndTasks().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ProjectMilestoneDepartmentDTO mapToDto(Project project) {
        ProjectMilestoneDepartmentDTO dto = new ProjectMilestoneDepartmentDTO();
        dto.setProjectID(  project.getProjectID());
        dto.setTitle(      project.getTitle());
        dto.setDescription(project.getDescription());
        dto.setContent(    project.getContent());
        dto.setStartTime(  project.getStartTime());
        dto.setEndTime(    project.getEndTime());
        String creatorId = project.getCreatedBy();
        if (creatorId != null) {
            userRepository.findById(creatorId).ifPresent(user -> {
                UserDTO u = modelMapper.map(user, UserDTO.class);
                dto.setCreatedByInfo(u);
            });
        }
        if (project.getDepartmentProjects() != null) {
            List<DepartmentDTO> depts = project.getDepartmentProjects().stream()
                    .map(dp -> modelMapper.map(dp.getDepartment(), DepartmentDTO.class))
                    .collect(Collectors.toList());
            dto.setDepartments(depts);
        } else {
            dto.setDepartments(Collections.emptyList());
        }
        if (project.getMilestones() != null) {
            List<MilestoneDTO> miles = project.getMilestones().stream()
                    .map(m -> modelMapper.map(m, MilestoneDTO.class))
                    .collect(Collectors.toList());
            dto.setMilestones(miles);
        } else {
            dto.setMilestones(Collections.emptyList());
        }

        return dto;
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
        Project project = projectRepository
                .findProjectWithMilestonesAndDepartmentsById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found: " + projectId));
        ProjectMilestoneDepartmentDTO dto = new ProjectMilestoneDepartmentDTO();
        dto.setProjectID(  project.getProjectID());
        dto.setTitle(      project.getTitle());
        dto.setDescription(project.getDescription());
        dto.setContent(    project.getContent());
        dto.setStartTime(  project.getStartTime());
        dto.setEndTime(    project.getEndTime());
        String creatorId = project.getCreatedBy();
        if (creatorId != null) {
            userRepository.findById(creatorId).ifPresent(user -> {
                UserDTO userDto = modelMapper.map(user, UserDTO.class);
                dto.setCreatedByInfo(userDto);
            });
        }
        if (project.getDepartmentProjects() != null && !project.getDepartmentProjects().isEmpty()) {
            List<DepartmentDTO> depts = project.getDepartmentProjects().stream()
                    .map(dp -> modelMapper.map(dp.getDepartment(), DepartmentDTO.class))
                    .collect(Collectors.toList());
            dto.setDepartments(depts);
        } else {
            dto.setDepartments(Collections.emptyList());
        }
        if (project.getMilestones() != null && !project.getMilestones().isEmpty()) {
            List<MilestoneDTO> miles = project.getMilestones().stream()
                    .map(m -> modelMapper.map(m, MilestoneDTO.class))
                    .collect(Collectors.toList());
            dto.setMilestones(miles);
        } else {
            dto.setMilestones(Collections.emptyList());
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
