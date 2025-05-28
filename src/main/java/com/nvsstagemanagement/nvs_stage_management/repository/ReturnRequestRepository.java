package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.enums.ReturnRequestStatus;
import com.nvsstagemanagement.nvs_stage_management.model.BorrowedAsset;
import com.nvsstagemanagement.nvs_stage_management.model.ReturnRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReturnRequestRepository extends JpaRepository <ReturnRequest,String>{
    List<ReturnRequest> findByStaffId(String staffId);
    List<ReturnRequest> findByStatus(ReturnRequestStatus status);
    Optional<ReturnRequest> findByAsset_AssetIDAndTask_TaskIDAndStatus(
            String assetId,
            String taskId,
            ReturnRequestStatus status
    );
    List<ReturnRequest> findByTask_Milestone_Project_ProjectID(String projectId);
    List<ReturnRequest> findByStaff_Department_DepartmentId(String departmentId);
}
