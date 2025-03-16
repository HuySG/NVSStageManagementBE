package com.nvsstagemanagement.nvs_stage_management.dto.location;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class LocationDTO {
    private String locationID;
    private String locationName;
    @Enumerated(EnumType.STRING)
    private String status;
}
