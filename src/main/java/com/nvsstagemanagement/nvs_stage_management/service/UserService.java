package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers(String username);

}
