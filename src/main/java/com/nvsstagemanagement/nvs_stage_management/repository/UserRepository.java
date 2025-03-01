package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.model.Department;
import com.nvsstagemanagement.nvs_stage_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String Email); // Giai quyết vấn đề bị null

    Optional<User> findByDepartmentAndRoleID_RoleName(Department department, String roleName);
    boolean existsByEmail(String email);

}
