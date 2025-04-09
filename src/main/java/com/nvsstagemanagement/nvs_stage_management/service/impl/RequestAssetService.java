package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.exception.NotEnoughAssetException;
import com.nvsstagemanagement.nvs_stage_management.dto.project.ProjectDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.*;
import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.user.UserDTO;
import com.nvsstagemanagement.nvs_stage_management.enums.AssetStatus;
import com.nvsstagemanagement.nvs_stage_management.enums.BorrowedAssetStatus;
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
    private final AssetUsageHistoryRepository assetUsageHistoryRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ReturnedAssetRepository returnedAssetRepository;
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
                            .orElseThrow(
                                    () -> new RuntimeException("Requester not found with ID: " + task.getAssignee()));
                    requestAsset.setCreateBy(requester.getId());
                }
            }
            if (dto.getAssetID() != null && !dto.getAssetID().isEmpty()) {
                Asset asset = assetRepository.findById(dto.getAssetID())
                        .orElseThrow(() -> new RuntimeException("Asset not found: " + dto.getAssetID()));
                requestAsset.setAsset(asset);
            } else if (dto.getCategoryID() != null && !dto.getCategoryID().isEmpty()) {
                Category category = categoryRepository.findById(dto.getCategoryID())
                        .orElseThrow(() -> new RuntimeException("Category not found: " + dto.getCategoryID()));
                if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
                    throw new IllegalArgumentException("Quantity must be > 0 for category-based requests.");
                }
                RequestAssetCategory rac = new RequestAssetCategory();
                rac.setRequestAsset(requestAsset);
                rac.setCategory(category);
                rac.setQuantity(dto.getQuantity());
                RequestAssetCategoryId racId = new RequestAssetCategoryId();
                racId.setRequestId(requestAsset.getRequestId());
                racId.setCategoryId(category.getCategoryID());
                rac.setId(racId);
                if (requestAsset.getRequestAssetCategories() == null) {
                    requestAsset.setRequestAssetCategories(new ArrayList<>());
                }
                requestAsset.getRequestAssetCategories().add(rac);
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
        Optional<RequestAsset> requestAsset = requestAssetRepository.findById(id);
        return modelMapper.map(requestAsset, RequestAssetDTO.class);
    }

    @Override
    public List<RequestAssetDTO> getRequestsByAssetId(String assetId) {
        List<RequestAsset> requests = requestAssetRepository.findByAssetID(assetId);
        return requests.stream()
                .map(request -> modelMapper.map(request, RequestAssetDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public RequestAssetDTO updateRequestAssetStatus(UpdateRequestAssetStatusDTO dto) {
        RequestAsset request = requestAssetRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new RuntimeException("Request not found: " + dto.getRequestId()));
        String newStatus = dto.getStatus();
        String approverId = dto.getApproverId();

        if ("PENDING_AM".equals(newStatus)) {
            request.setStatus(RequestAssetStatus.PENDING_AM.name());
            request.setApprovedByDL(approverId);
            request.setApprovedByDLTime(Instant.now());
        } else if ("AM_APPROVED".equals(newStatus)) {
            request.setStatus(RequestAssetStatus.AM_APPROVED.name());
            request.setApprovedByAM(approverId);
            request.setApprovedByAMTime(Instant.now());
        } else {
            request.setStatus(newStatus);
        }

        RequestAsset updated = requestAssetRepository.save(request);
        RequestAssetDTO result = modelMapper.map(updated, RequestAssetDTO.class);

        if (RequestAssetStatus.PENDING_AM.name().equals(updated.getStatus())) {
            User leader = userRepository.findById(approverId)
                    .orElseThrow(() -> new RuntimeException("Leader not found: " + approverId));
            result.setApprovedByDLName(leader.getFullName());
        } else if (RequestAssetStatus.AM_APPROVED.name().equals(updated.getStatus())) {
            User am = userRepository.findById(approverId)
                    .orElseThrow(() -> new RuntimeException("AM not found: " + approverId));
            result.setApprovedByAMName(am.getFullName());
        }
        return result;
    }

    @Override
    public List<DepartmentLeaderRequestDTO> getDepartmentLeaderRequests(String departmentId) {
        List<RequestAsset> requests = requestAssetRepository.findRequestsForDepartmentLeader(departmentId);
        return requests.stream().map(request -> {
            DepartmentLeaderRequestDTO dto = new DepartmentLeaderRequestDTO();
            dto.setRequestId(request.getRequestId());
            dto.setDescription(request.getDescription());
            dto.setStartTime(request.getStartTime());
            dto.setEndTime(request.getEndTime());
            dto.setStatus(request.getStatus());
            dto.setQuantity(null);
            if (request.getAsset() != null) {
                dto.setAsset(modelMapper.map(request.getAsset(), AssetDTO.class));
            }
            if (request.getTask() != null) {
                dto.setTask(modelMapper.map(request.getTask(), TaskDTO.class));
            }
            if (request.getCreateBy() != null) {
                User user = userRepository.findById(request.getCreateBy()).orElse(null);
                if (user != null) {
                    dto.setRequesterInfo(modelMapper.map(user, UserDTO.class));
                }
            }

            if (request.getRequestAssetCategories() != null && !request.getRequestAssetCategories().isEmpty()) {
                List<RequestAssetCategoryDTO> categoryDTOs = request.getRequestAssetCategories()
                        .stream()
                        .map(cat -> modelMapper.map(cat, RequestAssetCategoryDTO.class))
                        .collect(Collectors.toList());
                dto.setCategories(categoryDTOs);
            }

            if (request.getTask() != null && request.getTask().getMilestone() != null
                    && request.getTask().getMilestone().getProject() != null) {
                dto.setProjectInfo(modelMapper.map(request.getTask().getMilestone().getProject(), ProjectDTO.class));
            }

            dto.setBookingType(request.getBookingType() != null ? request.getBookingType().name() : null);
            dto.setRecurrenceCount(request.getRecurrenceCount());
            dto.setRecurrenceInterval(request.getRecurrenceInterval());

            return dto;
        }).collect(Collectors.toList());
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
        return requests.stream().map(request -> {

            RequestAssetDTO dto = modelMapper.map(request, RequestAssetDTO.class);

            if (request.getCreateBy() != null) {
                User user = userRepository.findById(request.getCreateBy()).orElse(null);
                if (user != null) {
                    dto.setRequesterInfo(modelMapper.map(user, UserDTO.class));
                }
            }

            if (request.getTask() != null && request.getTask().getMilestone() != null
                    && request.getTask().getMilestone().getProject() != null) {
                dto.setProjectInfo(modelMapper.map(request.getTask().getMilestone().getProject(), ProjectDTO.class));
            }

            if (request.getRequestAssetCategories() != null && !request.getRequestAssetCategories().isEmpty()) {
                List<RequestAssetCategoryDTO> categoryDTOs = request.getRequestAssetCategories()
                        .stream()
                        .map(cat -> {
                            RequestAssetCategoryDTO dtoCat = new RequestAssetCategoryDTO();
                            dtoCat.setCategoryID(cat.getCategory().getCategoryID());
                            dtoCat.setName(cat.getCategory().getName());
                            dtoCat.setQuantity(cat.getQuantity());
                            return dtoCat;
                        })
                        .collect(Collectors.toList());
                dto.setCategories(categoryDTOs);
            }

            return dto;
        }).collect(Collectors.toList());
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

        Optional<BorrowedAsset> latestBorrowOpt = borrowedAssetRepository.findLatestBorrowBefore(asset.getAssetID(),
                request.getStartTime());
        if (latestBorrowOpt.isPresent()) {
            LocalDateTime previousEnd = LocalDateTime.from(latestBorrowOpt.get().getEndTime());
            LocalDateTime newStart = LocalDateTime.ofInstant(request.getStartTime(), ZoneId.systemDefault());
            if (Duration.between(previousEnd, newStart).toDays() < 3) {
                throw new NotEnoughAssetException(
                        "Cannot borrow asset because previous borrowing ended less than 3 days before new request start time.");
            }
        }
        Asset availableAsset = asset;

        BorrowedAsset borrowed = new BorrowedAsset();
        borrowed.setBorrowedID(UUID.randomUUID().toString());
        borrowed.setAsset(availableAsset);
        borrowed.setTask(request.getTask());
        borrowed.setBorrowTime(Instant.from(LocalDateTime.now()));
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

        requestAsset.setBookingType(dto.getBookingType());
        requestAsset.setRecurrenceCount(dto.getRecurrenceCount());
        requestAsset.setRecurrenceInterval(dto.getRecurrenceInterval());

        Task task;
        if (dto.getTaskID() != null && !dto.getTaskID().isEmpty()) {
            task = taskRepository.findById(dto.getTaskID())
                    .orElseThrow(() -> new RuntimeException("Task not found: " + dto.getTaskID()));
            requestAsset.setTask(task);
            if (task.getAssignee() != null && !task.getAssignee().isEmpty()) {
                User requester = userRepository.findById(task.getAssignee())
                        .orElseThrow(() -> new RuntimeException("Requester not found with ID: " + task.getAssignee()));
                requestAsset.setCreateBy(requester.getId());
                List<String> reRequestStatuses = Arrays.asList(
                        RequestAssetStatus.LEADER_REJECTED.toString(),
                        RequestAssetStatus.REJECTED.toString(),
                        RequestAssetStatus.CANCELLED.toString(),
                        RequestAssetStatus.AM_APPROVED.toString());
                if (requestAssetRepository.existsByTaskAndStatusNotIn(task, reRequestStatuses)) {
                    throw new IllegalStateException("A booking request for this task is still active and cannot be submitted again.");
                }
            }
        } else {
            task = null;
        }

        Asset asset = assetRepository.findById(dto.getAssetID())
                .orElseThrow(() -> new RuntimeException("Asset not found: " + dto.getAssetID()));
        if (!AssetStatus.AVAILABLE.equals(asset.getStatus())) {
            throw new IllegalStateException("Asset is not available for booking.");
        }
        List<String> reRequestStatuses = Arrays.asList(
                RequestAssetStatus.LEADER_REJECTED.toString(),
                RequestAssetStatus.REJECTED.toString(),
                RequestAssetStatus.CANCELLED.toString(),
                RequestAssetStatus.AM_APPROVED.toString());

        if (task != null && requestAssetRepository.existsByTaskAndAssetAndStatusNotInAndTimeOverlap(
                task, asset, reRequestStatuses, dto.getStartTime(), dto.getEndTime())) {
            throw new IllegalStateException("This asset is already booked during the selected time.");
        }

        requestAsset.setAsset(asset);
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

            List<String> reRequestStatuses = Arrays.asList(
                    RequestAssetStatus.LEADER_REJECTED.toString(),
                    RequestAssetStatus.REJECTED.toString(),
                    RequestAssetStatus.CANCELLED.toString());

            if (requestAssetRepository.existsByTaskAndStatusNotIn(task, reRequestStatuses)) {
                throw new IllegalStateException(
                        "A request for this task is still active and cannot be submitted again.");
            }
            requestAsset.setTask(task);
            if (task.getAssignee() != null && !task.getAssignee().isEmpty()) {
                User requester = userRepository.findById(task.getAssignee())
                        .orElseThrow(() -> new RuntimeException("Requester not found with ID: " + task.getAssignee()));
                requestAsset.setCreateBy(requester.getId());
            }
        }

        List<CreateCategoryRequestItemDTO> categoryItems = dto.getCategories();
        if (categoryItems == null || categoryItems.isEmpty()) {
            throw new IllegalArgumentException("At least one category must be provided.");
        }

        List<RequestAssetCategory> requestAssetCategories = new ArrayList<>();
        for (CreateCategoryRequestItemDTO item : categoryItems) {
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException(
                        "Quantity must be provided and greater than 0 for each category request.");
            }
            Category category = categoryRepository.findById(item.getCategoryID())
                    .orElseThrow(() -> new RuntimeException("Category not found: " + item.getCategoryID()));

            RequestAssetCategory rac = new RequestAssetCategory();
            RequestAssetCategoryId racId = new RequestAssetCategoryId(requestAsset.getRequestId(),
                    category.getCategoryID());
            rac.setId(racId);
            rac.setRequestAsset(requestAsset);
            rac.setCategory(category);
            rac.setQuantity(item.getQuantity());

            requestAssetCategories.add(rac);
        }
        requestAsset.setRequestAssetCategories(requestAssetCategories);

        RequestAsset savedRequest = requestAssetRepository.save(requestAsset);
        RequestAssetDTO responseDto = modelMapper.map(savedRequest, RequestAssetDTO.class);

        if (savedRequest.getRequestAssetCategories() != null) {
            List<RequestAssetCategoryDTO> categoryDTOs = savedRequest.getRequestAssetCategories()
                    .stream()
                    .map(rac -> {
                        RequestAssetCategoryDTO categoryDTO = new RequestAssetCategoryDTO();
                        categoryDTO.setCategoryID(rac.getCategory().getCategoryID());
                        categoryDTO.setName(rac.getCategory().getName());
                        categoryDTO.setQuantity(rac.getQuantity());
                        return categoryDTO;
                    })
                    .collect(Collectors.toList());
            responseDto.setCategories(categoryDTOs);
        }

        if (savedRequest.getCreateBy() != null) {
            User requester = userRepository.findById(savedRequest.getCreateBy()).orElse(null);
            if (requester != null) {
                responseDto.setRequesterInfo(modelMapper.map(requester, UserDTO.class));
            }
        }
        return responseDto;
    }

    @Override
    public RequestAssetDTO acceptCategoryRequest(String requestId) {

        RequestAsset request = requestAssetRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        if (request.getRequestAssetCategories() == null || request.getRequestAssetCategories().isEmpty()) {
            throw new IllegalStateException("This is not a category-based request. Please use the appropriate API.");
        }
        request.setStatus(RequestAssetStatus.AM_APPROVED.name());

        RequestAsset updatedRequest = requestAssetRepository.save(request);
        return modelMapper.map(updatedRequest, RequestAssetDTO.class);
    }

    @Override
    public RequestAssetDTO acceptBooking(String requestId, String approverId) {
        RequestAsset request = requestAssetRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));
        if (request.getAsset() == null) {
            throw new IllegalStateException("This request is not asset-based and cannot be processed.");
        }
        String currentStatus = request.getStatus();
        if ("PENDING_LEADER".equals(currentStatus)) {
            request.setStatus(RequestAssetStatus.PENDING_AM.name());
            request.setApprovedByDL(approverId);
            request.setApprovedByDLTime(Instant.now());
            RequestAsset updatedRequest = requestAssetRepository.save(request);
            RequestAssetDTO dto = modelMapper.map(updatedRequest, RequestAssetDTO.class);
            User dlUser = userRepository.findById(approverId)
                    .orElseThrow(() -> new RuntimeException("Approver (DL) not found: " + approverId));
            dto.setApprovedByDLName(dlUser.getFullName());
            return dto;
        } else if ("PENDING_AM".equals(currentStatus)) {
            request.setStatus(RequestAssetStatus.AM_APPROVED.name());
            request.setApprovedByAM(approverId);
            request.setApprovedByAMTime(Instant.now());
            RequestAsset updatedRequest = requestAssetRepository.save(request);
            BorrowedAsset borrowed = new BorrowedAsset();
            borrowed.setBorrowedID(UUID.randomUUID().toString());
            borrowed.setAsset(request.getAsset());
            borrowed.setTask(request.getTask());
            borrowed.setBorrowTime(Instant.now());
            borrowed.setEndTime(request.getEndTime());
            borrowed.setStatus(BorrowedAssetStatus.BOOKED.name());
            borrowed.setDescription("Accepted booking request " + requestId);
            borrowedAssetRepository.save(borrowed);
            AssetUsageHistory usage = new AssetUsageHistory();
            usage.setUsageID(UUID.randomUUID().toString());
            usage.setAsset(request.getAsset());
            if (request.getTask() != null && request.getTask().getMilestone() != null
                    && request.getTask().getMilestone().getProject() != null) {
                usage.setProject(request.getTask().getMilestone().getProject());
            } else {
                throw new RuntimeException("Cannot retrieve project information from the request's task.");
            }
            if (request.getCreateBy() != null) {
                User user = userRepository.findById(request.getCreateBy()).orElse(null);
                usage.setUser(user);
            }
            usage.setStartDate(request.getStartTime());
            usage.setEndDate(request.getEndTime());
            usage.setStatus("In Use");
            assetUsageHistoryRepository.save(usage);
            RequestAssetDTO dto = modelMapper.map(updatedRequest, RequestAssetDTO.class);
            User amUser = userRepository.findById(approverId)
                    .orElseThrow(() -> new RuntimeException("Approver (AM) not found: " + approverId));
            dto.setApprovedByAMName(amUser.getFullName());
            return dto;
        } else if (RequestAssetStatus.AM_APPROVED.name().equals(currentStatus)) {
            throw new RuntimeException("Request already fully approved.");
        } else {
            throw new RuntimeException("Request is in an invalid state for approval: " + currentStatus);
        }
    }
    public List<RequestAssetDTO> getRequestByTask(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));
        List<RequestAsset> requests = requestAssetRepository.findByTask(task);
        return requests.stream()
                .map(requestAsset -> modelMapper.map(requestAsset, RequestAssetDTO.class))
                .collect(Collectors.toList());
    }
    @Override
    public CheckAvailabilityResult checkAssetAvailabilityAndReturnAssets(String requestId) {
        RequestAsset request = requestAssetRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        CheckAvailabilityResult result = new CheckAvailabilityResult();

        if (request.getAsset() != null) {

            String assetId = request.getAsset().getAssetID();
            boolean isBorrowed = borrowedAssetRepository.existsActiveBorrow(assetId);
            boolean isReturned = returnedAssetRepository.existsReturnedAssetByAssetID(assetId);

            if (!isBorrowed || (isBorrowed && isReturned)) {
                Asset asset = assetRepository.findById(assetId)
                        .orElseThrow(() -> new RuntimeException("Asset not found: " + assetId));
                AssetDTO assetDTO = modelMapper.map(asset, AssetDTO.class);
                result.addAvailableAsset(assetDTO);
                result.setAvailable(true);
                result.setMessage("Asset is available.");
            } else {
                result.setAvailable(false);
                result.setMessage("Asset is currently borrowed and not yet returned.");
            }
        } else if (request.getRequestAssetCategories() != null && !request.getRequestAssetCategories().isEmpty()) {

            for (RequestAssetCategory rac : request.getRequestAssetCategories()) {
                String categoryId = rac.getCategory().getCategoryID();
                int quantityRequested = rac.getQuantity();

                List<Asset> availableAssets = assetRepository.findAvailableAssetsByCategory(categoryId);

                if (availableAssets.size() < quantityRequested) {
                    result.addMissingCategory(rac.getCategory().getName(), quantityRequested - availableAssets.size());
                } else {
                    List<AssetDTO> assetDTOs = availableAssets.subList(0, quantityRequested)
                            .stream()
                            .map(asset -> modelMapper.map(asset, AssetDTO.class))
                            .collect(Collectors.toList());
                    result.getAvailableAssets().addAll(assetDTOs);
                }
            }

            if (result.getMissingCategories().isEmpty()) {
                result.setAvailable(true);
                result.setMessage("All categories have sufficient assets.");
            } else {
                result.setAvailable(false);
                result.setMessage("Some categories are missing required assets.");
            }
        } else {
            result.setAvailable(false);
            result.setMessage("Invalid request: No asset or categories specified.");
        }

        return result;
    }
}
