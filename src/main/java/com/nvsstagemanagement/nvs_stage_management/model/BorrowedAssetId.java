package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class BorrowedAssetId implements Serializable {
    @Column(name = "AssetID", length = 50)
    private String assetID;

    @Column(name = "TaskID", length = 50)
    private String taskID;
}
