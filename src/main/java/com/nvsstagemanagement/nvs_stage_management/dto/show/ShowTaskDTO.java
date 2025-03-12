package com.nvsstagemanagement.nvs_stage_management.dto.show;

import com.nvsstagemanagement.nvs_stage_management.dto.task.TaskDTO;
import lombok.Data;

import java.util.List;
@Data
public class ShowTaskDTO extends ShowDTO {
    private List<TaskDTO> tasks;
}
