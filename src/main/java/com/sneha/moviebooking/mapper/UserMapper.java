package com.sneha.moviebooking.mapper;

import com.sneha.moviebooking.dto.UserResponse;
import com.sneha.moviebooking.entity.User;

public class UserMapper {

    public static UserResponse toResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
