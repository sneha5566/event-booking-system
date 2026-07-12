package com.sneha.moviebooking.controller;

import com.sneha.moviebooking.dto.AdminDashboardResponse;
import com.sneha.moviebooking.dto.OrganizerDashboardResponse;
import com.sneha.moviebooking.dto.UserDashboardResponse;
import com.sneha.moviebooking.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(
        name = "Dashboard APIs",
        description = "Role-based dashboard APIs"
)
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(
            summary = "Organizer Dashboard",
            description = "Retrieve organizer dashboard statistics."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dashboard retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/organizer/dashboard")
    @PreAuthorize("hasRole('ORGANIZER')")
    public OrganizerDashboardResponse getOrganizerDashboard() {

        return dashboardService.getOrganizerDashboard();
    }

    @Operation(
            summary = "Admin Dashboard",
            description = "Retrieve system-wide admin dashboard statistics."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dashboard retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminDashboardResponse getAdminDashboard() {

        return dashboardService.getAdminDashboard();
    }

    @Operation(
            summary = "User Dashboard",
            description = "Retrieve the logged-in user's dashboard."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dashboard retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/user/dashboard")
    @PreAuthorize("hasRole('USER')")
    public UserDashboardResponse getUserDashboard() {

        return dashboardService.getUserDashboard();
    }
}