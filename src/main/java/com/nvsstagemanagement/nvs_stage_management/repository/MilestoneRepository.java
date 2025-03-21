package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.model.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MilestoneRepository  extends JpaRepository<Milestone,String> {
    List<Milestone> findByProject_ProjectID(String projectID);
    @Query("SELECT m FROM Milestone m " +
            "LEFT JOIN FETCH m.events " +
            "WHERE m.project.projectID = :projectID")
    List<Milestone> findMilestonesWithEventsByProjectID(@Param("projectID") String projectID);
}
