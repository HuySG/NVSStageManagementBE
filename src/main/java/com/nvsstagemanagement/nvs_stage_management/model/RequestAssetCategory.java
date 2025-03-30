package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "RequestAssetCategory")
public class RequestAssetCategory {

    @EmbeddedId
    private RequestAssetCategoryId id = new RequestAssetCategoryId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("requestId")
    @JoinColumn(name = "RequestId",referencedColumnName = "requestId",columnDefinition = "nvarchar(50)")
    private RequestAsset requestAsset;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    @JoinColumn(name = "CategoryID",referencedColumnName = "categoryId",columnDefinition = "nvarchar(50)")
    private Category category;

    @Column(name = "Quantity")
    private int quantity;
}
