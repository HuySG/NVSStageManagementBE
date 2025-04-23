package com.nvsstagemanagement.nvs_stage_management.dto.project;

import lombok.Data;

import java.util.List;

@Data
public class DepartmentProjectDTO extends ProjectDTO{
    private List<String> departmentID;
}
