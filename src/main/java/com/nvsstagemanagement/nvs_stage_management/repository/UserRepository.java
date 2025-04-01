package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.model.Department;
import com.nvsstagemanagement.nvs_stage_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
