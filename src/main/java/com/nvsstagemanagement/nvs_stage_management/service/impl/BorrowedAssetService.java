package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.BorrowedAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.BorrowedAssetsOverviewDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.ProjectBorrowedAssetsDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.borrowedAsset.StaffBorrowedAssetDTO;
import com.nvsstagemanagement.nvs_stage_management.enums.BorrowedAssetStatus;
import com.nvsstagemanagement.nvs_stage_management.model.*;
import com.nvsstagemanagement.nvs_stage_management.repository.*;
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
    private final RequestAssetRepository requestAssetRepository;
    private final RequestAssetAllocationRepository allocationRepository;
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
        return borrowedAssetRepository.findAll().stream().map(ba -> {
            BorrowedAssetDTO dto = modelMapper.map(ba, BorrowedAssetDTO.class);
            requestAssetRepository.findByTask_TaskIDAndAsset_AssetID(ba.getTask().getTaskID(), ba.getAsset().getAssetID())
                    .ifPresentOrElse(req -> dto.setRequestId(req.getRequestId()), () ->
                            allocationRepository.findByAsset_AssetIDAndRequestAsset_Task_TaskID(ba.getAsset().getAssetID(), ba.getTask().getTaskID())
                                    .ifPresent(alloc -> dto.setRequestId(alloc.getRequestAsset().getRequestId()))
                    );
            return dto;
        }).toList();
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
        List<String> statuses = List.of(
                BorrowedAssetStatus.BOOKED.name(),
                BorrowedAssetStatus.IN_USE.name()
        );
        List<BorrowedAsset> borrowedAssets = borrowedAssetRepository
                .findByStatusIn(statuses)
                .stream()
                .filter(ba -> ba.getTask() != null
                        && ba.getTask().getMilestone() != null
                        && ba.getTask().getMilestone().getProject() != null)
                .collect(Collectors.toList());
        Map<String, List<BorrowedAsset>> byProject = borrowedAssets.stream()
                .collect(Collectors.groupingBy(
                        ba -> ba.getTask().getMilestone().getProject().getProjectID()
                ));
        List<ProjectBorrowedAssetsDTO> projectDTOs = byProject.entrySet().stream()
                .map(entry -> {
                    String projectId = entry.getKey();
                    Project project = entry.getValue().get(0)
                            .getTask().getMilestone().getProject();
                    int deptCount = entry.getValue().stream()
                            .map(ba -> ba.getTask().getAssigneeUser())
                            .filter(Objects::nonNull)
                            .map(User::getDepartment)
                            .filter(Objects::nonNull)
                            .map(Department::getDepartmentId)
                            .collect(Collectors.toSet())
                            .size();

                    ProjectBorrowedAssetsDTO dto = new ProjectBorrowedAssetsDTO();
                    dto.setProjectId(projectId);
                    dto.setProjectTitle(project.getTitle());
                    dto.setDepartmentsUsing(deptCount);
                    dto.setBorrowedAssetsCount(entry.getValue().size());
                    return dto;
                })
                .collect(Collectors.toList());

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
        dto.setAssetId(ba.getAsset().getAssetID());
        dto.setAssetName(ba.getAsset().getAssetName());
        dto.setBorrowTime(ba.getBorrowTime());
        dto.setStartTime (ba.getStartTime());
        dto.setEndTime(ba.getEndTime());
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
