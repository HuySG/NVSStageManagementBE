package com.nvsstagemanagement.nvs_stage_management.dto.requestAsset;

import com.nvsstagemanagement.nvs_stage_management.enums.BookingType;
import lombok.Data;

import java.time.Instant;

@Data
public class CreateBookingRequestDTO {

    private String title;
    private String description;
    private Instant startTime;
    private Instant endTime;
    private String assetID;
    private String taskID;
    private BookingType bookingType;
    private Integer recurrenceCount;
    private Integer recurrenceInterval;
}
