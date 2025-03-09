package com.nvsstagemanagement.nvs_stage_management.service.impl;

import com.nvsstagemanagement.nvs_stage_management.dto.authentication.AuthenticatedUserDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.config.JwtGenerator;
import com.nvsstagemanagement.nvs_stage_management.dto.exception.InvalidValueException;
import com.nvsstagemanagement.nvs_stage_management.dto.exception.NotFoundException;
import com.nvsstagemanagement.nvs_stage_management.dto.user.UserDTO;
import com.nvsstagemanagement.nvs_stage_management.model.User;
import com.nvsstagemanagement.nvs_stage_management.repository.DepartmentRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.RoleRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.UserRepository;
import com.nvsstagemanagement.nvs_stage_management.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    @Autowired
    private final AuthenticationManager authenticationManager;
    private final JwtGenerator jwtGenerator;
    private final PasswordEncoder passwordEncoder;
//    private LoggerService loggerService;

    @Override
    public List<UserDTO> getAllUser() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getUserByName(String name) {
        List<User> users = userRepository.findAll();

        users.removeIf((u) -> !(u.getFullName().contains(name)));
        return users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());

    }

    @Override
    public AuthenticatedUserDTO login(User user) {
        User u = userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new NotFoundException("Không tồn tại tài khoản này"));
        if(u.getStatus().equals("Inactive")) {
            throw new InvalidValueException("Tài khoản đã bị khóa");
        }
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        AuthenticatedUserDTO authenticatedUser = new AuthenticatedUserDTO();
        authenticatedUser.setToken(token);
        authenticatedUser.setRoles(roles);
        return authenticatedUser;
    }

    @Override
    public AuthenticatedUserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        User userEntity = modelMapper.map(userDTO, User.class);
        userEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userEntity.setCreateDate(Instant.now());
        userEntity.setId(UUID.randomUUID().toString());
        userEntity.setCreateDate(Instant.now());
        userEntity.setStatus("Active");
        User saved = userRepository.save(userEntity);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(saved.getEmail(), null, Collections.emptyList());
        String token = jwtGenerator.generateToken(authToken);

        List<String> roles = saved.getRole() != null
                ? List.of(String.valueOf(saved.getRole()))
                : Collections.emptyList();
        return new AuthenticatedUserDTO(token, roles);
    }
}
