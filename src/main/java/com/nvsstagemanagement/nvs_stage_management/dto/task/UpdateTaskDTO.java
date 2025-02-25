package com.nvsstagemanagement.nvs_stage_management.dto.task;

import lombok.Data;

import java.util.List;

@Data
public class UpdateTaskDTO extends TaskDTO{
    private List<TaskUserDTO> taskUserDTO;
}
