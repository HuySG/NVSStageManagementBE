package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.exception.NotEnoughAssetException;
import com.nvsstagemanagement.nvs_stage_management.dto.requestAsset.CreateCategoryRequestItemDTO;
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

            // Sinh RequestId mới
            requestAsset.setRequestId(UUID.randomUUID().toString());
            requestAsset.setTitle(dto.getTitle());
            requestAsset.setDescription(dto.getDescription());
            requestAsset.setStartTime(dto.getStartTime());
            requestAsset.setEndTime(dto.getEndTime());
            requestAsset.setRequestTime(Instant.now());
            requestAsset.setStatus(RequestAssetStatus.PENDING_LEADER.toString());

            // Xử lý liên kết với Task và lấy thông tin người yêu cầu (createBy)
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

            // Nếu cung cấp assetID, xử lý branch asset-based
            if (dto.getAssetID() != null && !dto.getAssetID().isEmpty()) {
                Asset asset = assetRepository.findById(dto.getAssetID())
                        .orElseThrow(() -> new RuntimeException("Asset not found: " + dto.getAssetID()));
                requestAsset.setAsset(asset);
            }
            // Nếu cung cấp categoryID thì xử lý theo branch category-based với bảng trung gian
            else if (dto.getCategoryID() != null && !dto.getCategoryID().isEmpty()) {
                Category category = categoryRepository.findById(dto.getCategoryID())
                        .orElseThrow(() -> new RuntimeException("Category not found: " + dto.getCategoryID()));
                if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
                    throw new IllegalArgumentException("Quantity must be > 0 for category-based requests.");
                }
                // Tạo đối tượng RequestAssetCategory để lưu thông tin mối quan hệ với quantity
                RequestAssetCategory rac = new RequestAssetCategory();
                rac.setRequestAsset(requestAsset);
                rac.setCategory(category);
                rac.setQuantity(dto.getQuantity());

                // Khởi tạo composite key với RequestId và CategoryID
                RequestAssetCategoryId racId = new RequestAssetCategoryId();
                racId.setRequestId(requestAsset.getRequestId());
                racId.setCategoryId(category.getCategoryID());
                rac.setId(racId);

                // Nếu collection chưa được khởi tạo, khởi tạo nó
                if (requestAsset.getRequestAssetCategories() == null) {
                    requestAsset.setRequestAssetCategories(new ArrayList<>());
                }
                requestAsset.getRequestAssetCategories().add(rac);
            } else {
                throw new RuntimeException("Either assetID or categoryID must be provided.");
            }

            // Lưu đối tượng RequestAsset (cascade sẽ tự lưu cả RequestAssetCategory nếu có)
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
        return requests.stream().map(request -> {
            RequestAssetDTO dto = modelMapper.map(request, RequestAssetDTO.class);
            if (request.getCreateBy() != null) {
                User user = userRepository.findById(request.getCreateBy()).orElse(null);
                if (user != null) {
                    UserDTO userDTO = modelMapper.map(user, UserDTO.class);
                    dto.setRequesterInfo(userDTO);
                }
            }

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

        Task task;
        if (dto.getTaskID() != null && !dto.getTaskID().isEmpty()) {
            task = taskRepository.findById(dto.getTaskID())
                    .orElseThrow(() -> new RuntimeException("Task not found: " + dto.getTaskID()));
            requestAsset.setTask(task);
            if (task.getAssignee() != null && !task.getAssignee().isEmpty()) {
                User requester = userRepository.findById(task.getAssignee())
                        .orElseThrow(() -> new RuntimeException("Requester not found with ID: " + task.getAssignee()));
                requestAsset.setCreateBy(requester.getId());
            }
        } else {
            task = null;
        }

        Asset asset = assetRepository.findById(dto.getAssetID())
                .orElseThrow(() -> new RuntimeException("Asset not found: " + dto.getAssetID()));

        List<String> reRequestStatuses = Arrays.asList(
                RequestAssetStatus.LEADER_REJECTED.toString(),
                RequestAssetStatus.REJECTED.toString(),
                RequestAssetStatus.CANCELLED.toString()
        );
        if (task != null && requestAssetRepository.existsByTaskAndAssetAndStatusNotIn(task, asset, reRequestStatuses)) {
            throw new IllegalStateException("This room has already been requested for this task and is still active.");
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
                    RequestAssetStatus.CANCELLED.toString()
            );

            if (requestAssetRepository.existsByTaskAndStatusNotIn(task, reRequestStatuses)) {
                throw new IllegalStateException("A request for this task is still active and cannot be submitted again.");
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
                throw new IllegalArgumentException("Quantity must be provided and greater than 0 for each category request.");
            }
            Category category = categoryRepository.findById(item.getCategoryID())
                    .orElseThrow(() -> new RuntimeException("Category not found: " + item.getCategoryID()));

            RequestAssetCategory rac = new RequestAssetCategory();
            RequestAssetCategoryId racId = new RequestAssetCategoryId(requestAsset.getRequestId(), category.getCategoryID());
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


}
