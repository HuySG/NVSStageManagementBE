package com.nvsstagemanagement.nvs_stage_management.service;

import com.nvsstagemanagement.nvs_stage_management.model.User;
import com.nvsstagemanagement.nvs_stage_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public List<User> getAllUsers(String username) {
        List<User> users = userRepository.findAll();
        users.removeIf((u) -> !(u.getFullName().contains(username)));
        return users;
    }
}
