package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.dto.show.DepartmentShowDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.show.ShowDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.show.ShowTaskDTO;

import java.util.List;

public interface IShowService {
    List<ShowDTO> getAllShow();
    ShowDTO createShow(ShowDTO showDTO);
    DepartmentShowDTO assignDepartmentToShow(DepartmentShowDTO departmentShowDTO);
    List<ShowTaskDTO> getAllShowWithTasks();
    List<ShowDTO> getShowWithUserId(String userId);
}
