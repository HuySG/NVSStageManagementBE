package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.exception.NotEnoughAssetException;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.*;
import com.nvsstagemanagement.nvs_stage_management.dto.user.UserDTO;
import com.nvsstagemanagement.nvs_stage_management.enums.AssetStatus;
import com.nvsstagemanagement.nvs_stage_management.enums.RequestAssetStatus;
import com.nvsstagemanagement.nvs_stage_management.model.*;
import com.nvsstagemanagement.nvs_stage_management.repository.*;
import com.nvsstagemanagement.nvs_stage_management.service.IRequestAssetService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestAssetService implements IRequestAssetService {
    private final RequestAssetRepository requestAssetRepository;
    private final AssetRepository assetRepository;
    private final BorrowedAssetRepository borrowedAssetRepository;
    private final ProjectAssetPermissionRepository projectAssetPermissionRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;


    @Override
    public List<RequestAssetDTO> getAllRequest() {
        List<RequestAsset> requests = requestAssetRepository.findAll();
        return requests.stream()
                .map(request -> modelMapper.map(request, RequestAssetDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestAssetDTO> createRequest(List<CreateRequestAssetDTO> dtos) {
        List<RequestAssetDTO> responses = new ArrayList<>();

        for (CreateRequestAssetDTO dto : dtos) {
            RequestAsset requestAsset = new RequestAsset();

            requestAsset.setRequestId(UUID.randomUUID().toString());
            requestAsset.setTitle(dto.getTitle());
            requestAsset.setDescription(dto.getDescription());
            requestAsset.setStartTime(dto.getStartTime());
            requestAsset.setEndTime(dto.getEndTime());
            requestAsset.setRequestTime(Instant.now());
            requestAsset.setStatus(RequestAssetStatus.PENDING_LEADER.toString());

            if (dto.getTaskID() != null && !dto.getTaskID().isEmpty()) {
                Task task = taskRepository.findById(dto.getTaskID())
                        .orElseThrow(() -> new RuntimeException("Task not found: " + dto.getTaskID()));
                requestAsset.setTask(task);
                if (task.getAssignee() != null && !task.getAssignee().isEmpty()) {
                    User requester = userRepository.findById(task.getAssignee())
                            .orElseThrow(() -> new RuntimeException("Requester not found with ID: " + task.getAssignee()));

                    requestAsset.setCreateBy(requester.getId());
                }
            }

            if (dto.getAssetID() != null && !dto.getAssetID().isEmpty()) {

                Asset asset = assetRepository.findById(dto.getAssetID())
                        .orElseThrow(() -> new RuntimeException("Asset not found: " + dto.getAssetID()));
                requestAsset.setAsset(asset);
                requestAsset.setQuantity(1);
            } else if (dto.getCategoryID() != null && !dto.getCategoryID().isEmpty()) {
                Category category = categoryRepository.findById(dto.getCategoryID())
                        .orElseThrow(() -> new RuntimeException("Category not found: " + dto.getCategoryID()));
                requestAsset.setCategory(category);
                if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
                    throw new IllegalArgumentException("Quantity must be > 0 for category-based requests.");
                }
                requestAsset.setQuantity(dto.getQuantity());
            } else {
                throw new RuntimeException("Either assetID or categoryID must be provided.");
            }

            RequestAsset savedRequest = requestAssetRepository.save(requestAsset);
            RequestAssetDTO responseDto = modelMapper.map(savedRequest, RequestAssetDTO.class);
            if (savedRequest.getCreateBy() != null) {
                User requester = userRepository.findById(savedRequest.getCreateBy())
                        .orElse(null);
                if (requester != null) {
                    UserDTO requesterDTO = modelMapper.map(requester, UserDTO.class);
                    responseDto.setRequesterInfo(requesterDTO);
                }
            }

            responses.add(responseDto);
        }
        return responses;
    }

    @Override
    public RequestAssetDTO getRequestById(String id) {
       Optional< RequestAsset> requestAsset = requestAssetRepository.findById(id);
       return modelMapper.map(requestAsset, RequestAssetDTO.class);
    }

    @Override
    public RequestAssetDTO updateRequestAssetStatus(UpdateRequestAssetStatusDTO dto) {
        RequestAsset request = requestAssetRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new RuntimeException("Request not found: " + dto.getRequestId()));
        request.setStatus(dto.getStatus());
        RequestAsset updated = requestAssetRepository.save(request);
        return modelMapper.map(updated, RequestAssetDTO.class);
    }
    @Override
    public List<RequestAssetDTO> getRequestsForLeader(String departmentId) {
        List<RequestAsset> requests = requestAssetRepository.findRequestsForDepartmentLeader(departmentId);
        return requests.stream()
                .map(request -> modelMapper.map(request, RequestAssetDTO.class))
                .collect(Collectors.toList());
    }
    @Override
    public List<RequestAssetDTO> getRequestsByUser(String userId) {
        List<RequestAsset> requests = requestAssetRepository.findByUserId(userId);
        return requests.stream()
                .map(r -> modelMapper.map(r, RequestAssetDTO.class))
                .collect(Collectors.toList());
    }
    @Override
    public List<RequestAssetDTO> getRequestsForAssetManager() {
        List<String> allowedStatuses = Arrays.asList("PENDING_AM", "AM_APPROVED", "REJECTED", "CANCELLED");
        List<RequestAsset> requests = requestAssetRepository.findByStatusIn(allowedStatuses);
        return requests.stream()
                .map(request -> modelMapper.map(request, RequestAssetDTO.class))
                .collect(Collectors.toList());
    }
    @Override
    public RequestAssetDTO acceptRequest(String requestId) {

        RequestAsset request = requestAssetRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));
        if (RequestAssetStatus.AM_APPROVED.name().equals(request.getStatus())) {
            throw new NotEnoughAssetException("Request already accepted.");
        }

        Asset asset = assetRepository.findById(request.getAsset().getAssetID())
                .orElseThrow(() -> new RuntimeException("Asset not found: " + request.getAsset().getAssetID()));
        String projectTypeID = request.getTask().getMilestone().getProject().getProjectType().getProjectTypeID();
        String assetTypeID = asset.getAssetType().getAssetTypeID();
        ProjectAssetPermissionId permissionId = new ProjectAssetPermissionId(projectTypeID, assetTypeID);
        ProjectAssetPermission permission = projectAssetPermissionRepository.findById(permissionId)
                .orElseThrow(() -> new NotEnoughAssetException(
                        "No permission found for projectType=" + projectTypeID + " and assetType=" + assetTypeID));
        if (!permission.getAllowed()) {
            throw new NotEnoughAssetException("Asset type " + assetTypeID
                    + " is not allowed for project type " + projectTypeID);
        }
        if (asset.getStatus() != null && asset.getStatus().equals(AssetStatus.MAINTENANCE)) {
            throw new NotEnoughAssetException("Asset is under maintenance and cannot be borrowed.");
        }

        Optional<BorrowedAsset> latestBorrowOpt = borrowedAssetRepository.findLatestBorrowBefore(asset.getAssetID(), request.getStartTime());
        if (latestBorrowOpt.isPresent()) {
            LocalDateTime previousEnd = LocalDateTime.from(latestBorrowOpt.get().getEndTime());
            LocalDateTime newStart = LocalDateTime.ofInstant(request.getStartTime(), ZoneId.systemDefault());
            if (Duration.between(previousEnd, newStart).toDays() < 3) {
                throw new NotEnoughAssetException("Cannot borrow asset because previous borrowing ended less than 3 days before new request start time.");
            }
        }
        Asset availableAsset = asset;

        BorrowedAsset borrowed = new BorrowedAsset();
        borrowed.setBorrowedID(UUID.randomUUID().toString());
        borrowed.setAsset(availableAsset);
        borrowed.setTask(request.getTask());
        borrowed.setBorrowTime(LocalDateTime.now());
        borrowed.setEndTime(request.getEndTime());
        borrowed.setDescription("Accepted request " + requestId);
        borrowedAssetRepository.save(borrowed);

        request.setStatus(RequestAssetStatus.AM_APPROVED.name());
        RequestAsset updatedRequest = requestAssetRepository.save(request);

        return modelMapper.map(updatedRequest, RequestAssetDTO.class);
    }
    @Override
    public RequestAssetDTO createBookingRequest(CreateBookingRequestDTO dto) {
        RequestAsset requestAsset = new RequestAsset();

        requestAsset.setRequestId(UUID.randomUUID().toString());
        requestAsset.setTitle(dto.getTitle());
        requestAsset.setDescription(dto.getDescription());
        requestAsset.setStartTime(dto.getStartTime());
        requestAsset.setEndTime(dto.getEndTime());
        requestAsset.setRequestTime(Instant.now());
        requestAsset.setStatus(RequestAssetStatus.PENDING_LEADER.toString());

        if (dto.getTaskID() != null && !dto.getTaskID().isEmpty()) {
            Task task = taskRepository.findById(dto.getTaskID())
                    .orElseThrow(() -> new RuntimeException("Task not found: " + dto.getTaskID()));
            requestAsset.setTask(task);
            if (task.getAssignee() != null && !task.getAssignee().isEmpty()) {
                User requester = userRepository.findById(task.getAssignee())
                        .orElseThrow(() -> new RuntimeException("Requester not found with ID: " + task.getAssignee()));
                requestAsset.setCreateBy(requester.getId());
            }
        }
        Asset asset = assetRepository.findById(dto.getAssetID())
                .orElseThrow(() -> new RuntimeException("Asset not found: " + dto.getAssetID()));
        requestAsset.setAsset(asset);
        requestAsset.setQuantity(1);
        RequestAsset savedRequest = requestAssetRepository.save(requestAsset);
        RequestAssetDTO responseDto = modelMapper.map(savedRequest, RequestAssetDTO.class);
        if (savedRequest.getCreateBy() != null) {
            User requester = userRepository.findById(savedRequest.getCreateBy()).orElse(null);
            if (requester != null) {
                responseDto.setRequesterInfo(modelMapper.map(requester, UserDTO.class));
            }
        }
        return responseDto;
    }
    @Override
    public RequestAssetDTO createCategoryRequest(CreateCategoryRequestDTO dto) {
        RequestAsset requestAsset = new RequestAsset();

        requestAsset.setRequestId(UUID.randomUUID().toString());
        requestAsset.setTitle(dto.getTitle());
        requestAsset.setDescription(dto.getDescription());
        requestAsset.setStartTime(dto.getStartTime());
        requestAsset.setEndTime(dto.getEndTime());
        requestAsset.setRequestTime(Instant.now());
        requestAsset.setStatus(RequestAssetStatus.PENDING_LEADER.toString());

        if (dto.getTaskID() != null && !dto.getTaskID().isEmpty()) {
            Task task = taskRepository.findById(dto.getTaskID())
                    .orElseThrow(() -> new RuntimeException("Task not found: " + dto.getTaskID()));
            requestAsset.setTask(task);
            if (task.getAssignee() != null && !task.getAssignee().isEmpty()) {
                User requester = userRepository.findById(task.getAssignee())
                        .orElseThrow(() -> new RuntimeException("Requester not found with ID: " + task.getAssignee()));
                requestAsset.setCreateBy(requester.getId());
            }
        }
        Category category = categoryRepository.findById(dto.getCategoryID())
                .orElseThrow(() -> new RuntimeException("Category not found: " + dto.getCategoryID()));
        requestAsset.setCategory(category);
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be provided and greater than 0 for category requests.");
        }
        requestAsset.setQuantity(dto.getQuantity());

        RequestAsset savedRequest = requestAssetRepository.save(requestAsset);
        RequestAssetDTO responseDto = modelMapper.map(savedRequest, RequestAssetDTO.class);
        if (savedRequest.getCreateBy() != null) {
            User requester = userRepository.findById(savedRequest.getCreateBy()).orElse(null);
            if (requester != null) {
                responseDto.setRequesterInfo(modelMapper.map(requester, UserDTO.class));
            }
        }
        return responseDto;
    }
}
