package com.nvsstagemanagement.nvs_stage_management.dto.show;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
public class ShowDTO {
    private String showID;
    private String title;
    private String description;
    private String content;
    private Instant startTime;
    private Instant endTime;
    private String department;
    private String createdBy;
}
