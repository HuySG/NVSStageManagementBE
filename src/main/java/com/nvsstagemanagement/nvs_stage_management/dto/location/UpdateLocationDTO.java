package com.nvsstagemanagement.nvs_stage_management.dto.location;

import com.nvsstagemanagement.nvs_stage_management.enums.LocationStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateLocationDTO {
    @Size(max = 50, message = "Location name must not exceed 50 characters.")
    private String locationName;

    @Size(max = 50, message = "Status must not exceed 50 characters.")
    private LocationStatus status;
}
