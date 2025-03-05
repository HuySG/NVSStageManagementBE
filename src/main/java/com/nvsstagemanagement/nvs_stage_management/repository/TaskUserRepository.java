package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.model.Task;
import com.nvsstagemanagement.nvs_stage_management.model.TaskUser;
import com.nvsstagemanagement.nvs_stage_management.model.TaskUserId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskUserRepository extends JpaRepository<TaskUser, TaskUserId> {
    List<TaskUser> findByTask_TaskID(String taskId);
    @Modifying
    @Transactional
    void deleteByTask(Task task);
}
