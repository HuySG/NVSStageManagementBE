package com.nvsstagemanagement.nvs_stage_management.dto.requestAsset;

import com.nvsstagemanagement.nvs_stage_management.dto.asset.AssetDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskDTO;
import com.nvsstagemanagement.nvs_stage_management.model.Asset;
import com.nvsstagemanagement.nvs_stage_management.model.Task;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Data
public class RequestAssetDTO {
    private String requestId;
    private Integer quantity;
    private String discription;
    private Instant startTime;
    private Instant endTime;
    private AssetDTO asset;
    private TaskDTO task;
}
