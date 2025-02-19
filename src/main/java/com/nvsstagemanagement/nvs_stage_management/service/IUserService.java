package com.nvsstagemanagement.nvs_stage_management.service;



import com.nvsstagemanagement.nvs_stage_management.dto.authentication.AuthenticatedUserDTO;
import com.nvsstagemanagement.nvs_stage_management.dto.user.UserDTO;
import com.nvsstagemanagement.nvs_stage_management.model.User;

import java.util.List;

public interface IUserService {
    List<UserDTO> getAllUser();
    List<UserDTO> getUserByName(String name);
    AuthenticatedUserDTO login(User user);
}
