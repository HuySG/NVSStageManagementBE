package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.BorrowedAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.BorrowedAssetsOverviewDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.ProjectBorrowedAssetsDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.StaffBorrowedAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.enums.BorrowedAssetStatus;
import com.nvsstagemanagement.nvs_stage_management.model.*;
import com.nvsstagemanagement.nvs_stage_management.repository.AssetRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.BorrowedAssetRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.TaskRepository;
import com.nvsstagemanagement.nvs_stage_management.service.IBorrowedAssetService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BorrowedAssetService implements IBorrowedAssetService {


    private final BorrowedAssetRepository borrowedAssetRepository;
    private final AssetRepository assetRepository;
    private final TaskRepository taskRepository;

    private final ModelMapper modelMapper;

    @Override
    public BorrowedAssetDTO createBorrowedAsset(BorrowedAssetDTO dto) {
        BorrowedAsset borrowedAsset = new BorrowedAsset();

        borrowedAsset.setBorrowedID(UUID.randomUUID().toString());
        borrowedAsset.setBorrowTime(dto.getBorrowTime());
        borrowedAsset.setStartTime(dto.getStartTime());
        borrowedAsset.setEndTime(dto.getEndTime());
        borrowedAsset.setDescription(dto.getDescription());

        Asset asset = assetRepository.findById(dto.getAssetID())
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        Task task = taskRepository.findById(dto.getTaskID())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        borrowedAsset.setAsset(asset);
        borrowedAsset.setTask(task);
        borrowedAsset.setStatus(BorrowedAssetStatus.BOOKED.name());

        borrowedAssetRepository.save(borrowedAsset);

        return modelMapper.map(borrowedAsset, BorrowedAssetDTO.class);
    }

    @Override
    public List<BorrowedAssetDTO> getAllBorrowedAssets() {
        return borrowedAssetRepository.findAll()
                .stream()
                .map(asset -> modelMapper.map(asset, BorrowedAssetDTO.class))
                .toList();
    }

    @Override
    public Optional<BorrowedAssetDTO> getBorrowedAssetById(String borrowedId) {
        return borrowedAssetRepository.findById(borrowedId)
                .map(asset -> modelMapper.map(asset, BorrowedAssetDTO.class));
    }

    @Override
    public void deleteBorrowedAsset(String borrowedId) {
        borrowedAssetRepository.deleteById(borrowedId);
    }
    @Override
    public BorrowedAssetsOverviewDTO getBorrowedAssetsOverview() {
        List<BorrowedAsset> borrowedAssets = borrowedAssetRepository.findAll()
                .stream()
                .filter(ba -> BorrowedAssetStatus.BOOKED.name().equals(ba.getStatus())
                        || BorrowedAssetStatus.IN_USE.name().equals(ba.getStatus()))
                .toList();

        Map<String, List<BorrowedAsset>> projectGrouped = borrowedAssets.stream()
                .collect(Collectors.groupingBy(ba -> ba.getTask().getMilestone().getProject().getProjectID()));

        List<ProjectBorrowedAssetsDTO> projectDTOs = new ArrayList<>();

        for (Map.Entry<String, List<BorrowedAsset>> entry : projectGrouped.entrySet()) {
            String projectId = entry.getKey();
            List<BorrowedAsset> projectAssets = entry.getValue();
            Project project = projectAssets.get(0).getTask().getMilestone().getProject();

            Set<String> departments = projectAssets.stream()
                    .map(ba -> {
                        User assigneeUser = ba.getTask().getAssigneeUser();
                        if (assigneeUser != null && assigneeUser.getDepartment() != null) {
                            return assigneeUser.getDepartment().getDepartmentId();
                        } else {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            ProjectBorrowedAssetsDTO dto = new ProjectBorrowedAssetsDTO();
            dto.setProjectId(projectId);
            dto.setProjectTitle(project.getTitle());
            dto.setDepartmentsUsing(departments.size());
            dto.setBorrowedAssetsCount(projectAssets.size());

            projectDTOs.add(dto);
        }

        BorrowedAssetsOverviewDTO overview = new BorrowedAssetsOverviewDTO();
        overview.setTotalBorrowedAssets(borrowedAssets.size());
        overview.setProjects(projectDTOs);

        return overview;
    }
    @Override
    public List<StaffBorrowedAssetDTO> getBorrowedAssetsByStaff(String staffId) {
        List<BorrowedAsset> inUse   = borrowedAssetRepository
                .findByTask_AssigneeAndStatus(staffId, BorrowedAssetStatus.IN_USE.name());
        List<BorrowedAsset> overdue = borrowedAssetRepository
                .findByTask_AssigneeAndStatus(staffId, BorrowedAssetStatus.OVERDUE.name());
        return Stream.concat(inUse.stream(), overdue.stream())
                .map(this::toDto)
                .collect(Collectors.toList());
    }


    private StaffBorrowedAssetDTO toDto(BorrowedAsset ba) {
        StaffBorrowedAssetDTO dto = new StaffBorrowedAssetDTO();
        dto.setBorrowedID(ba.getBorrowedID());
        dto.setAssetId   (ba.getAsset().getAssetID());
        dto.setAssetName (ba.getAsset().getAssetName());
        dto.setBorrowTime(ba.getBorrowTime());
        dto.setStartTime (ba.getStartTime());
        dto.setEndTime   (ba.getEndTime());
        dto.setStatus    (ba.getStatus());
        dto.setTaskId    (ba.getTask().getTaskID());
        dto.setTaskTitle (ba.getTask().getTitle());
        if (ba.getTask().getMilestone() != null
                && ba.getTask().getMilestone().getProject() != null) {
            dto.setProjectId(
                    ba.getTask()
                            .getMilestone()
                            .getProject()
                            .getProjectID()
            );
        }
        return dto;
    }
}
