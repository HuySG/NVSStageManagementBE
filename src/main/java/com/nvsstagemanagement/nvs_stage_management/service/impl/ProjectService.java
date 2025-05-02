package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.department.DepartmentDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.milestone.MilestoneDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.project.*;
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
                .map(project -> {
                    ProjectDTO dto = modelMapper.map(project, ProjectDTO.class);
                    ProjectType type = project.getProjectType();
                    if (type != null) {
                        dto.setProjectTypeID(type.getProjectTypeID());
                        dto.setProjectTypeName(type.getTypeName());
                    }
                    return dto;
                })
                .toList();
    }


    @Override
    public ProjectDTO createProject(CreateProjectDTO dto) {

        if (dto.getStartTime() != null && dto.getEndTime() != null) {
            if (dto.getEndTime().isBefore(dto.getStartTime())) {
                throw new IllegalArgumentException("End time must be after start time.");
            }
        }
        ProjectType projectType = projectTypeRepository.findById(dto.getProjectTypeID())
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
    /**
     * Đánh dấu project là COMPLETED. Có thể bỏ qua điều kiện nếu force = true.
     */
    @Override
    public void markProjectAsCompleted(String projectId, boolean force) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found: " + projectId));

        if (project.getStatus() == ProjectStatus.COMPLETED) {
            throw new IllegalStateException("Project is already completed.");
        }

        List<Milestone> milestones = project.getMilestones();
        boolean hasUnfinished = milestones != null && milestones.stream()
                .anyMatch(m -> m.getStatus() == null || !m.getStatus().name().equals("COMPLETED"));

        if (hasUnfinished && !force) {
            throw new RuntimeException("Some milestones are not completed. Use ?force=true to override.");
        }

        project.setStatus(ProjectStatus.COMPLETED);
        project.setActualEndTime(Instant.now());

        projectRepository.save(project);
    }
    /**
     * Huỷ một dự án dựa trên ID. Hàm này sẽ thực hiện các bước:
     *
     * - Kiểm tra xem dự án có tồn tại hay không. Nếu không, ném lỗi.
     * - Nếu dự án đã hoàn thành thì không được phép huỷ, ném lỗi.
     * - Đánh dấu trạng thái của dự án là "CANCELLED" và ghi nhận thời gian kết thúc thực tế.
     * - Với tất cả các milestone trong dự án:
     *     + Đánh dấu milestone là "CANCELLED".
     *     + Với mỗi task thuộc milestone đó:
     *         * Đổi trạng thái task thành "Archived".
     *         * Tự động huỷ các yêu cầu mượn tài sản nếu chưa được phê duyệt hoặc đã bị huỷ.
     *
     * Sau khi thực hiện, toàn bộ dữ liệu liên quan đến milestone, task, request sẽ được cập nhật tương ứng.
     *
     * @param projectId ID của dự án cần huỷ
     * @throws RuntimeException nếu không tìm thấy dự án
     * @throws IllegalStateException nếu dự án đã hoàn thành
     */
    @Override
    public void cancelProject(String projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found: " + projectId));

        if (project.getStatus() == ProjectStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel finished project");
        }

        project.setStatus(ProjectStatus.CANCELLED);
        project.setActualEndTime(Instant.now());
        projectRepository.save(project);

        if (project.getMilestones() != null) {
            project.getMilestones().forEach(milestone -> {
                milestone.setStatus(MilestoneStatus.CANCELLED);

                if (milestone.getTasks() != null) {
                    milestone.getTasks().forEach(task -> {
                        task.setStatus(TaskEnum.Archived);
                        taskRepository.save(task);

                        List<RequestAsset> requests = requestAssetRepository.findByTask(task);
                        for (RequestAsset request : requests) {
                            if (!request.getStatus().equals(RequestAssetStatus.AM_APPROVED.name()) &&
                                    !request.getStatus().equals(RequestAssetStatus.CANCELLED.name())) {
                                request.setStatus(RequestAssetStatus.CANCELLED.name());
                                request.setRejectionReason("Request canceled because project canceled");
                                requestAssetRepository.save(request);
                            }
                        }
                    });
                }

                milestoneRepository.save(milestone);
            });
        }

        System.out.println("Project has been canceled and all milestone and task related");
    }


}
