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
    Optional<User> findByEmail(String Email); // Giai quyết vấn đề bị null

    Optional<User> findByDepartmentAndRole_RoleName(Department department, String roleName);
    boolean existsByEmail(String email);
    List<User> findByDepartment(Department department);
    List<User> findByDepartment_DepartmentId(String departmentId);
    /**
     * Tìm các leader của phòng ban trong một dự án cụ thể
     *
     * @param departmentId ID của phòng ban
     * @param projectId ID của dự án
     * @return Danh sách các user là leader của phòng ban trong dự án
     */
    @Query("SELECT u FROM User u " +
            "WHERE u.department.departmentId = :departmentId " +
            "AND u.role.roleName = 'DEPARTMENT_LEADER' " +
            "AND EXISTS (SELECT 1 FROM DepartmentProject dp " +
            "           WHERE dp.department.departmentId = :departmentId " +
            "           AND dp.project.projectID = :projectId)")
    List<User> findLeadersByDepartmentAndProject(
            @Param("departmentId") String departmentId,
            @Param("projectId") String projectId
    );

}
