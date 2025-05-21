package com.nvsstagemanagement.nvs_stage_management.service;


import com.nvsstagemanagement.nvs_stage_management.dto.returnAsset.ReturnRequestDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.returnAsset.ReturnRequestResponseDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.returnAsset.ProcessReturnRequestDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.returnAsset.ReturnRequestStatisticsDTO;

import java.util.List;

public interface IReturnRequestService {
    /**
     * Tạo một yêu cầu trả tài sản.
     *
     * @param dto DTO chứa thông tin yêu cầu trả
     * @param staffId ID của staff gửi yêu cầu
     * @return Yêu cầu vừa được tạo dưới dạng DTO
     */
    ReturnRequestResponseDTO createReturnRequest(ReturnRequestDTO dto, String staffId);

    /**
     * Xử lý yêu cầu trả tài sản (Leader xác nhận hoặc từ chối yêu cầu).
     *
     * @param dto DTO chứa thông tin xử lý yêu cầu
     * @param leaderId ID của leader thực hiện xử lý
     * @return Yêu cầu đã được xử lý dưới dạng DTO
     */
    ReturnRequestResponseDTO processReturnRequest(
            String requestId,
            ProcessReturnRequestDTO dto,
            String leaderId
    );

    /**
     * Lấy danh sách tất cả yêu cầu trả tài sản của một staff cụ thể.
     *
     * @param staffId ID của staff
     * @return Danh sách các yêu cầu trả tài sản dạng DTO
     */
    List<ReturnRequestResponseDTO> getStaffRequests(String staffId);

    /**
     * Lấy danh sách tất cả các yêu cầu trả tài sản đang chờ xử lý (PENDING).
     *
     * @return Danh sách các yêu cầu đang chờ xử lý dưới dạng DTO
     */
    List<ReturnRequestResponseDTO> getPendingRequests();
    /**
     * Lấy thông tin chi tiết của một yêu cầu trả tài sản theo ID.
     *
     * @param requestId ID của yêu cầu trả tài sản cần truy vấn
     * @return ReturnRequestResponseDTO chứa thông tin chi tiết của yêu cầu bao gồm:
     *         - Thông tin tài sản được trả
     *         - Thông tin task liên quan
     *         - Thông tin người yêu cầu trả
     *         - Trạng thái yêu cầu
     *         - Thời gian yêu cầu và xử lý
     *         - Lý do từ chối (nếu có)
     * @throws RuntimeException nếu không tìm thấy yêu cầu với ID tương ứng
     */
    ReturnRequestResponseDTO getReturnRequestById(String requestId);

    /**
     * Lấy danh sách các yêu cầu trả tài sản của một phòng ban cụ thể.
     * Bao gồm tất cả các yêu cầu của nhân viên thuộc phòng ban đó.
     *
     * @param departmentId ID của phòng ban cần truy vấn
     * @return Danh sách các ReturnRequestResponseDTO, mỗi DTO chứa thông tin:
     *         - Chi tiết yêu cầu trả
     *         - Thông tin người yêu cầu
     *         - Trạng thái xử lý
     *         - Thời gian yêu cầu và xử lý
     * @throws RuntimeException nếu không tìm thấy phòng ban với ID tương ứng
     */
    List<ReturnRequestResponseDTO> getDepartmentRequests(String departmentId);

    /**
     * Lấy danh sách các yêu cầu trả tài sản liên quan đến một dự án cụ thể.
     * Bao gồm tất cả các yêu cầu trả tài sản được sử dụng trong các task của dự án.
     *
     * @param projectId ID của dự án cần truy vấn
     * @return Danh sách các ReturnRequestResponseDTO, mỗi DTO chứa thông tin:
     *         - Chi tiết yêu cầu trả
     *         - Thông tin task trong dự án
     *         - Trạng thái xử lý
     *         - Thời gian yêu cầu và xử lý
     * @throws RuntimeException nếu không tìm thấy dự án với ID tương ứng
     */
    List<ReturnRequestResponseDTO> getProjectRequests(String projectId);

    /**
     * Lấy thống kê tổng quan về các yêu cầu trả tài sản trong hệ thống.
     * Bao gồm các chỉ số thống kê theo phòng ban và dự án.
     *
     * @return ReturnRequestStatisticsDTO chứa các thông tin thống kê:
     *         - Tổng số yêu cầu và phân loại theo trạng thái
     *         - Số lượng trả trễ và phí phạt
     *         - Thống kê chi tiết theo từng phòng ban
     *         - Thống kê chi tiết theo từng dự án
     *         - Tổng phí phạt (trễ hạn và hư hỏng)
     */
    ReturnRequestStatisticsDTO getReturnRequestStatistics();


}