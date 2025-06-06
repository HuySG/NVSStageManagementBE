package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.model.Department;
import com.nvsstagemanagement.nvs_stage_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByFullName(String name);
    Optional<User> findByEmail(String Email);

    boolean existsByEmail(String email);
    List<User> findByDepartment(Department department);
    List<User> findByDepartment_DepartmentId(String departmentId);

    @Query("SELECT u FROM User u " +
            "WHERE u.department.departmentId = :departmentId " +
            "AND u.role.roleName = 'Leader' " +
            "AND EXISTS (SELECT 1 FROM DepartmentProject dp " +
            "           WHERE dp.department.departmentId = :departmentId " +
            "           AND dp.project.projectID = :projectId)")
    List<User> findLeadersByDepartmentAndProject(
            @Param("departmentId") String departmentId,
            @Param("projectId") String projectId
    );
    List<User> findByRole_Id(Integer roleId);
}
