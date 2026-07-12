package com.sneha.moviebooking.service;

import com.sneha.moviebooking.dto.UpdateRoleRequest;
import com.sneha.moviebooking.dto.UserResponse;
import com.sneha.moviebooking.entity.Role;
import com.sneha.moviebooking.entity.User;
import com.sneha.moviebooking.mapper.UserMapper;
import com.sneha.moviebooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.sneha.moviebooking.util.SecurityUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    public UserResponse updateRole(Long id, UpdateRoleRequest request) {

        User currentUser = securityUtil.getCurrentUser();

        if (currentUser.getId().equals(id)
                && request.getRole() != currentUser.getRole()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You cannot change your own role.");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"));

        if (user.getRole() == Role.ADMIN && !currentUser.getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot change another admin's role.");
        }

        user.setRole(request.getRole());

        User updatedUser = userRepository.save(user);

        return UserMapper.toResponse(updatedUser);
    }
}
