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
    /**
     * Tìm danh sách yêu cầu trả tài sản bởi Staff ID.
     *
     * @param staffId ID của nhân viên
     * @return Danh sách yêu cầu trả tài sản
     */
    List<ReturnRequest> findByStaffId(String staffId);

    /**
     * Tìm danh sách yêu cầu trả tài sản theo trạng thái.
     *
     * @param status Trạng thái yêu cầu
     * @return Danh sách yêu cầu trả tài sản
     */
    List<ReturnRequest> findByStatus(ReturnRequestStatus status);
    Optional<ReturnRequest> findByAsset_AssetIDAndTask_TaskIDAndStatus(
            String assetId,
            String taskId,
            ReturnRequestStatus status
    );
    Optional<BorrowedAsset> findByAsset_AssetIDAndTask_TaskID(
            String assetId,
            String taskId
    );

    List<ReturnRequest> findByTask_Milestone_Project_ProjectID(String projectId);
    List<ReturnRequest> findByTask_Milestone_Project_DepartmentProjects_Department_DepartmentId(String departmentId);
    List<ReturnRequest> findByStaff_Department_DepartmentId(String departmentId);
}
