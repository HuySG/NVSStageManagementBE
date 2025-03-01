package com.nvsstagemanagement.nvs_stage_management.service;


import com.nvsstagemanagement.nvs_stage_management.dto.department.DepartmentDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.department.DepartmentWithUserDTO;

import java.util.List;

public interface IDepartmentService {
  List<DepartmentWithUserDTO> getDepartmentWithUser();
}
