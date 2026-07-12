package com.sneha.moviebooking.controller;

import com.sneha.moviebooking.dto.AuthenticationResponse;
import com.sneha.moviebooking.dto.LoginRequest;
import com.sneha.moviebooking.dto.SignupRequest;
import com.sneha.moviebooking.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication APIs",
        description = "User registration and login APIs"
)
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Register User",
            description = "Register a new user in the system."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/signup")
    public String signup(@Valid @RequestBody SignupRequest request) {
        return authService.signup(request);
    }

    @Operation(
            summary = "Login",
            description = "Authenticate a user and return a JWT access token."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public AuthenticationResponse login(
            @Valid @RequestBody LoginRequest request) {

        return authService.login(request);
    }
}