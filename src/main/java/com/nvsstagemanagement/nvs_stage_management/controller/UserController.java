package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.dto.authentication.AuthenticatedUserDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.user.UserDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.user.UserLoginDTO;
import com.nvsstagemanagement.nvs_stage_management.model.User;
import com.nvsstagemanagement.nvs_stage_management.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUser();
    }
    @GetMapping("/search")
    public List<UserDTO> getUsersByName(String name){
        return userService.getUserByName(name);
    }
    @PostMapping("login")
    public ResponseEntity<AuthenticatedUserDTO> login(@Valid @RequestBody UserLoginDTO userRequestDTO){
        User user = new User();
        user.setPassword(userRequestDTO.getPassword());
        user.setEmail(userRequestDTO.getEmail());
        AuthenticatedUserDTO authenticatedUserDTO = userService.login(user);
        return ResponseEntity.status(HttpStatus.OK).body(authenticatedUserDTO);
    }
    @PostMapping("/register")
    public ResponseEntity<AuthenticatedUserDTO> createUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.createUser(userDTO));
    }
}
