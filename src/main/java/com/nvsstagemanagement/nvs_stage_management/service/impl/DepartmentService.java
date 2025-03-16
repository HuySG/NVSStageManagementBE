package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.department.DepartmentWithUserDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.user.UserDTO;
import com.nvsstagemanagement.nvs_stage_management.model.Department;
import com.nvsstagemanagement.nvs_stage_management.model.User;
import com.nvsstagemanagement.nvs_stage_management.repository.DepartmentRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.UserRepository;
import com.nvsstagemanagement.nvs_stage_management.service.IDepartmentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService implements IDepartmentService {
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    @Override
    public List<DepartmentWithUserDTO> getDepartmentWithUser() {
        List<Department> departments = departmentRepository.findAll();

        return departments.stream().map(dept -> {
            DepartmentWithUserDTO dto = new DepartmentWithUserDTO();
            dto.setId(dept.getDepartmentId());
            dto.setName(dept.getName());
            dto.setDescription(dept.getDescription());

            List<User> users = userRepository.findByDepartment(dept);
            List<UserDTO> userDTOs = users.stream()
                    .map(user -> modelMapper.map(user, UserDTO.class))
                    .collect(Collectors.toList());
            dto.setUsers(userDTOs);

            return dto;
        }).collect(Collectors.toList());
    }
}
