package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.taskUsers tu LEFT JOIN FETCH tu.user WHERE t.show.showID = :showId")
    List<Task> findTasksWithUsersByShowId(String showId);
    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.taskUsers tu LEFT JOIN FETCH tu.user WHERE t.taskID = :taskId")
    Optional<Task> findTaskWithUsersByTaskId(String taskId);
}