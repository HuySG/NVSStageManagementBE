package com.nvsstagemanagement.nvs_stage_management.controller;

import com.nvsstagemanagement.nvs_stage_management.model.User;
import com.nvsstagemanagement.nvs_stage_management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/user")
public class UserController {
    private UserService userService;

    @Autowired // Injection
    public UserController (UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/getalluser")
    public ResponseEntity<List<User>> getAllUsers(@RequestParam(required = false) String username) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.getAllUsers(username));
    }

}
