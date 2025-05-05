package com.nvsstagemanagement.nvs_stage_management.model;

import com.nvsstagemanagement.nvs_stage_management.enums.AllocationImageType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "AllocationImage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AllocationImage {
    @Id
    @Column(name = "ImageID", length = 50, columnDefinition = "nvarchar(50)")
    private String imageId;

    @Lob
    @Column(name = "ImageUrl", nullable = false)
    private String imageUrl;

    @Column(name = "Type", length = 20)
    private AllocationImageType imageType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AllocationID", referencedColumnName = "AllocationID", nullable = false)
    private RequestAssetAllocation allocation;
}
