package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.enums.AllocationImageType;
import com.nvsstagemanagement.nvs_stage_management.model.AllocationImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllocationImageRepository extends JpaRepository<AllocationImage,String> {
    List<AllocationImage> findByAllocation_AllocationIdAndImageType(String allocationId, AllocationImageType imageType);
}
