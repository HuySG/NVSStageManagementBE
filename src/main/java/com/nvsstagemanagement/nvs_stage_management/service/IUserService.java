package com.nvsstagemanagement.nvs_stage_management.service;


import com.nvsstagemanagement.nvs_stage_management.dto.user.*;

import java.util.List;

public interface IUserService {
    UserDTO createUser(UserCreationRequest request);
    UserDTO getMyInfo();
    UserResponse updateUser(String userId, UserUpdateRequest request);
    void deleteUser(String userId);
    UserResponse getUser(String userId);
    List<UserDTO> getUsers();
    UserResponse activationUser (ActivationUserRequest activationUserRequest);
    List<UserDTO> getUsersByDepartmentId(String departmentId);
}
