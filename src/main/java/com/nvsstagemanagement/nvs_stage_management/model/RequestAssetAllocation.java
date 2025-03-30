package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "RequestAssetAllocation")
public class RequestAssetAllocation {

    @Id
    @Nationalized
    @Column(name = "AllocationID", length = 50, nullable = false)
    private String allocationId;
    @Column(name = "Note")
    private String note;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RequestId", referencedColumnName = "RequestId", nullable = false)
    private RequestAsset requestAsset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CategoryID", referencedColumnName = "CategoryID", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AssetID", referencedColumnName = "AssetID", nullable = false)
    private Asset asset;


}
