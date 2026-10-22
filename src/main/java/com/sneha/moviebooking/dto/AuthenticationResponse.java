package com.sneha.moviebooking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;
    private Long userId;
    private String name;
    private String email;
    private String role;
}
