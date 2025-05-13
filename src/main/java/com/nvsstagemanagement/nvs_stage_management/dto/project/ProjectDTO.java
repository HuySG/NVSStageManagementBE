package com.nvsstagemanagement.nvs_stage_management.dto.project;

import com.nvsstagemanagement.nvs_stage_management.dto.department.DepartmentDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.user.UserDTO;
import com.nvsstagemanagement.nvs_stage_management.enums.ProjectStatus;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class ProjectDTO {
    private String projectID;
    private String title;
    private String description;
    private String content;
    private Instant startTime;
    private Instant endTime;
    private UserDTO createdByInfo;
    private Integer projectTypeID;
    private String projectTypeName;
    private ProjectStatus status;
    private List<DepartmentDTO> departments;

}
