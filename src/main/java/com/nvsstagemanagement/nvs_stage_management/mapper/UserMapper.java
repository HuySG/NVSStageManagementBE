package com.nvsstagemanagement.nvs_stage_management.mapper;


import com.nvsstagemanagement.nvs_stage_management.dto.user.UserCreationRequest;
import com.nvsstagemanagement.nvs_stage_management.dto.user.UserUpdateRequest;
import com.nvsstagemanagement.nvs_stage_management.dto.user.UserResponse;
import com.nvsstagemanagement.nvs_stage_management.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toUserResponse(User user);

    @Mapping(target = "role", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
