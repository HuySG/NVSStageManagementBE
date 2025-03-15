package com.nvsstagemanagement.nvs_stage_management.config;

import com.nvsstagemanagement.nvs_stage_management.model.Department;
import com.nvsstagemanagement.nvs_stage_management.model.Role;
import com.nvsstagemanagement.nvs_stage_management.model.User;
import com.nvsstagemanagement.nvs_stage_management.repository.DepartmentRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.RoleRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

//@Component
public class DataInitializer implements CommandLineRunner {



    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository,
                           DepartmentRepository departmentRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "staff@gmail.com";
        if (!userRepository.existsByEmail(adminEmail)) {
            Department department = departmentRepository.findById("13F2F427-48B6-485F-836D-BEBBBFD6D9D6")
                    .orElseThrow(() -> new RuntimeException("Department not found"));

            Role role = roleRepository.findById("4")
                    .orElseThrow(() -> new RuntimeException("Role not found"));

            User admin = new User();
            admin.setId(UUID.randomUUID().toString());
            admin.setFullName("Nguyễn Văn Staff");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("abc123"));
            admin.setDepartment(department);
            admin.setRole(role);
            admin.setStatus("Active");
            admin.setCreateDate(Instant.now());

            userRepository.save(admin);
            System.out.println("Admin account created successfully.");
        } else {
            System.out.println("Admin account already exists.");
        }
    }
}