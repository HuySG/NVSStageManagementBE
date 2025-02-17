package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.model.TaskUser;
import com.nvsstagemanagement.nvs_stage_management.model.TaskUserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskUserRepository extends JpaRepository<TaskUser, TaskUserId> {
}
