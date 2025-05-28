package com.nvsstagemanagement.nvs_stage_management.repository;

import com.nvsstagemanagement.nvs_stage_management.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findById(String id);
}
