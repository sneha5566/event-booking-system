package com.sneha.moviebooking.controller;

import com.sneha.moviebooking.dto.UpdateRoleRequest;
import com.sneha.moviebooking.dto.UserResponse;
import com.sneha.moviebooking.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public List<UserResponse> getAllUsers() {

        return userService.getAllUsers();
    }

    @PatchMapping("/users/{id}/role")
    public UserResponse updateRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest request) {

        return userService.updateRole(id, request);
    }

}
