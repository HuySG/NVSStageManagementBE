package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, String> {
    @Query("SELECT DISTINCT p FROM Show p LEFT JOIN FETCH p.tasks")
    List<Show> findAllWithTasks();
    @Query(value = "SELECT DISTINCT p.* FROM \"User\" u " +
            "JOIN Department d ON u.DepartmentId = d.ID " +
            "JOIN DepartmentShow dp ON dp.DepartmentId = d.ID " +
            "JOIN Show p ON dp.ShowId = p.ShowID " +
            "WHERE u.ID = :userId", nativeQuery = true)
    List<Show> findShowByUserId(@Param("userId") String userId);
}
