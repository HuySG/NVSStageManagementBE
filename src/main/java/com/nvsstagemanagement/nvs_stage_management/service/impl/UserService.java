package com.nvsstagemanagement.nvs_stage_management.service.impl;

//import com.nvsstagemanagement.nvs_stage_management.dto.config.JwtGenerator;
//import com.nvsstagemanagement.nvs_stage_management.dto.exception.InvalidValueException;
//import com.nvsstagemanagement.nvs_stage_management.dto.exception.NotFoundException;

import com.nvsstagemanagement.nvs_stage_management.dto.role.RoleDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.user.*;
import com.nvsstagemanagement.nvs_stage_management.exception.AppException;
import com.nvsstagemanagement.nvs_stage_management.exception.ErrorCode;
import com.nvsstagemanagement.nvs_stage_management.mapper.UserMapper;
import com.nvsstagemanagement.nvs_stage_management.model.Department;
import com.nvsstagemanagement.nvs_stage_management.model.Role;
import com.nvsstagemanagement.nvs_stage_management.model.User;
import com.nvsstagemanagement.nvs_stage_management.repository.DepartmentRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.RoleRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.UserRepository;
import com.nvsstagemanagement.nvs_stage_management.service.IUserService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {

    private final DepartmentRepository departmentRepository;

    private final ModelMapper modelMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

        public UserDTO createUser(UserCreationRequest request) {

            if(userRepository.existsByEmail(request.getEmail())){
                throw new AppException(ErrorCode.EmailAlreadyExist);
            }else {
                User createUser = modelMapper.map(request, User.class);
                createUser.setId(UUID.randomUUID().toString());
                createUser.setPassword(passwordEncoder.encode(request.getPassword()));


                User user = new User();
                try {
                    user = userRepository.save(createUser);

                } catch (DataIntegrityViolationException exception) {
                    throw new AppException(ErrorCode.USER_EXISTED);
                }

                // send infor email vs password cho user luôn
                emailService.sendEmail(user);

                return modelMapper.map(user, UserDTO.class);
            }

        }

    // api getUserinfo by using context -. get name find by name
    public UserDTO getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByFullName(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return modelMapper.map(user,UserDTO.class);
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role role = roleRepository.findById(request.getRoleId());
        user.setRole(role);
        Department department = departmentRepository.findByDepartmentId(request.getDepartmentId());
        user.setDepartment(department);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    public List<UserDTO> getUsers() {
        log.info("In method get Users");

        return userRepository.findAll().stream().map(user ->
                modelMapper.map(user, UserDTO.class)).collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        UserResponse response = userMapper.toUserResponse(user);
        if (user.getRole() != null) {
            response.setRoleID(String.valueOf(user.getRole().getId()));
        }

        return response;
    }



    @Override
    public UserResponse activationUser(ActivationUserRequest activationUserRequest) {

        User existingUser = userRepository.findByEmail(activationUserRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(activationUserRequest.getEmail() + " not found"));

        existingUser.setPassword(passwordEncoder.encode(activationUserRequest.getNewPassword()));
        boolean authenticated = passwordEncoder.matches(activationUserRequest.getNewPassword(), existingUser.getPassword());
        System.out.println(authenticated);
        // Save the updated user with the new password
        return userMapper.toUserResponse(userRepository.save(existingUser));
    }
    @Override
    public List<UserDTO> getUsersByDepartmentId(String departmentId) {
        List<User> users = userRepository.findByDepartment_DepartmentId(departmentId);
        return users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }
}
