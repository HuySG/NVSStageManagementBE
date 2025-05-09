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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

//@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

    }

//    @Override
//    public void run(String... args) throws Exception {
//
//        List<Department> allDepartments = departmentRepository.findAll();
//
//        // ===== 1. Admin =====
//        if (!userRepository.existsByEmail("admin@gmail.com")) {
//            Optional<Role> adminRole = roleRepository.findById("1");
//            Department anyDepartment = allDepartments.get(0);
//
//            User admin = new User();
//            admin.setId(UUID.randomUUID().toString());
//            admin.setFullName("Admin System");
//            admin.setEmail("admin@gmail.com");
//            admin.setPassword(passwordEncoder.encode("admin123"));
//            admin.setDepartment(anyDepartment);
//            admin.setRole(adminRole);
//            admin.setStatus("Active");
//            admin.setCreateDate(Instant.now());
//            userRepository.save(admin);
//        }
//
//        // ===== 2. ConcertMaster =====
//        if (!userRepository.existsByEmail("concertmaster@gmail.com")) {
//            Role cmRole = roleRepository.findById(2);
//            Department anyDepartment = allDepartments.get(0);
//
//            User concertMaster = new User();
//            concertMaster.setId(UUID.randomUUID().toString());
//            concertMaster.setFullName("Nguyễn Văn ConcertMaster");
//            concertMaster.setEmail("concertmaster@gmail.com");
//            concertMaster.setPassword(passwordEncoder.encode("abc123"));
//            concertMaster.setDepartment(anyDepartment);
//            concertMaster.setRole(cmRole);
//            concertMaster.setStatus("Active");
//            concertMaster.setCreateDate(Instant.now());
//            userRepository.save(concertMaster);
//        }
//
//        // ===== 3. 8 Staff =====
//        Role staffRole = roleRepository.findById(3);
//
//        for (int i = 0; i < 8; i++) {
//            String email = "staff" + (i + 1) + "@gmail.com";
//            if (userRepository.existsByEmail(email)) continue;
//
//            Department dept = allDepartments.get(i % allDepartments.size());
//
//            User staff = new User();
//            staff.setId(UUID.randomUUID().toString());
//            staff.setFullName("Nhân viên " + (i + 1));
//            staff.setEmail(email);
//            staff.setPassword(passwordEncoder.encode("abc123"));
//            staff.setDepartment(dept);
//            staff.setRole(staffRole);
//            staff.setStatus("Active");
//            staff.setCreateDate(Instant.now());
//            userRepository.save(staff);
//        }
//
//        // ===== 4. 3 Leader (trừ phòng chỉ huy) =====
//        Role leaderRole = roleRepository.findById(4);
//
//        List<Department> nonConductingDepts = allDepartments.stream()
//                .filter(d -> !d.getName().equalsIgnoreCase("Phòng Chỉ Huy"))
//                .toList();
//
//        for (int i = 0; i < 3; i++) {
//            String email = "leader" + (i + 1) + "@gmail.com";
//            if (userRepository.existsByEmail(email)) continue;
//
//            Department dept = nonConductingDepts.get(i % nonConductingDepts.size());
//
//            User leader = new User();
//            leader.setId(UUID.randomUUID().toString());
//            leader.setFullName("Trưởng bộ phận " + (i + 1));
//            leader.setEmail(email);
//            leader.setPassword(passwordEncoder.encode("abc123"));
//            leader.setDepartment(dept);
//            leader.setRole(leaderRole);
//            leader.setStatus("Active");
//            leader.setCreateDate(Instant.now());
//            userRepository.save(leader);
//        }
//
//        System.out.println("✅ Users initialized: Admin, ConcertMaster, Staff x8, Leader x3");
//    }
}

