package com.nvsstagemanagement.nvs_stage_management.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TaskDependencyId  implements Serializable {
    private String taskID;
    private String dependsOnTaskID;
}
