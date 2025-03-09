package com.nvsstagemanagement.nvs_stage_management.repository;


import com.nvsstagemanagement.nvs_stage_management.model.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {}
