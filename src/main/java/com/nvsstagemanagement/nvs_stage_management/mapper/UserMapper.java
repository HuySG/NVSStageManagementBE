package com.nvsstagemanagement.nvs_stage_management.mapper;


import com.nvsstagemanagement.nvs_stage_management.dto.request.UserCreationRequest;
import com.nvsstagemanagement.nvs_stage_management.dto.request.UserUpdateRequest;
import com.nvsstagemanagement.nvs_stage_management.dto.response.UserResponse;
import com.nvsstagemanagement.nvs_stage_management.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "role", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
