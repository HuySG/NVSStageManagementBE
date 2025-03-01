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

@Service
@RequiredArgsConstructor
public class DepartmentService implements IDepartmentService {
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    @Override
    public List<DepartmentWithUserDTO> getDepartmentWithUser() {
        List<Department> departments = departmentRepository.findAll();
        List<DepartmentWithUserDTO> dtos = new ArrayList<>();

        for (Department dept : departments) {
            Optional<User> leaderOpt = userRepository.findByDepartmentAndRoleID_RoleName(dept, "Leader");
            if (leaderOpt.isPresent()) {
                User leader = leaderOpt.get();
                UserDTO leaderDto = modelMapper.map(leader, UserDTO.class);
                DepartmentWithUserDTO dto = new DepartmentWithUserDTO();
                dto.setId(dept.getId());
                dto.setName(dept.getName());
                dto.setDescription(dept.getDescription());
                dto.setLeader(leaderDto);
                dtos.add(dto);
            }
        }
        return dtos;
    }
}
