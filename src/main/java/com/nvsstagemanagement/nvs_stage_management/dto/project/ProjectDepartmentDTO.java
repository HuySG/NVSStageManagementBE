package com.nvsstagemanagement.nvs_stage_management.dto.project;

import com.nvsstagemanagement.nvs_stage_management.dto.user.UserDTO;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
public class ProjectDepartmentDTO {
    private String projectID;
    private String title;
    private String description;
    private String content;
    private Instant startTime;
    private Instant endTime;
    private UserDTO createdByInfo;
}
