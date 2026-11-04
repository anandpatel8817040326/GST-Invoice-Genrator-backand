package com.gstinvoice.auth.controller;

import com.gstinvoice.auth.dto.AuthDto;
import com.gstinvoice.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication & User Management", description = "Endpoints for signup, signin, token rotation and RBAC")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthDto.ApiResponse<AuthDto.AuthResponse>> register(@Valid @RequestBody AuthDto.RegisterRequest request) {
        AuthDto.AuthResponse response = authService.register(request);
        return new ResponseEntity<>(
                AuthDto.ApiResponse.<AuthDto.AuthResponse>builder()
                        .success(true)
                        .message("User registered successfully")
                        .data(response)
                        .build(),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    @Operation(summary = "Login and obtain JWT tokens")
    public ResponseEntity<AuthDto.ApiResponse<AuthDto.AuthResponse>> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        AuthDto.AuthResponse response = authService.login(request);
        return ResponseEntity.ok(
                AuthDto.ApiResponse.<AuthDto.AuthResponse>builder()
                        .success(true)
                        .message("Login successful")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/email-login")
    @Operation(summary = "Direct login with only email")
    public ResponseEntity<AuthDto.ApiResponse<AuthDto.AuthResponse>> directEmailLogin(@Valid @RequestBody AuthDto.DirectEmailLoginRequest request) {
        AuthDto.AuthResponse response = authService.directEmailLogin(request.getEmail());
        return ResponseEntity.ok(
                AuthDto.ApiResponse.<AuthDto.AuthResponse>builder()
                        .success(true)
                        .message("Direct email login successful")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Rotate refresh token and retrieve new access token")
    public ResponseEntity<AuthDto.ApiResponse<AuthDto.AuthResponse>> refreshToken(@Valid @RequestBody AuthDto.RefreshTokenRequest request) {
        AuthDto.AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(
                AuthDto.ApiResponse.<AuthDto.AuthResponse>builder()
                        .success(true)
                        .message("Token refreshed successfully")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/logout")
    @Operation(summary = "Revoke user tokens and logout")
    public ResponseEntity<AuthDto.ApiResponse<Void>> logout(@RequestHeader(value = "X-User-Email", required = false) String headerEmail,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : headerEmail;
        if (email != null) {
            authService.logout(email);
        }
        return ResponseEntity.ok(
                AuthDto.ApiResponse.<Void>builder()
                        .success(true)
                        .message("Logged out successfully")
                        .build()
        );
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset token")
    public ResponseEntity<AuthDto.ApiResponse<String>> forgotPassword(@Valid @RequestBody AuthDto.ForgotPasswordRequest request) {
        String token = authService.forgotPassword(request);
        return ResponseEntity.ok(
                AuthDto.ApiResponse.<String>builder()
                        .success(true)
                        .message("Password reset token generated")
                        .data(token)
                        .build()
        );
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using reset token")
    public ResponseEntity<AuthDto.ApiResponse<Void>> resetPassword(@Valid @RequestBody AuthDto.ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(
                AuthDto.ApiResponse.<Void>builder()
                        .success(true)
                        .message("Password updated successfully. You can now login.")
                        .build()
        );
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Verify email address via token")
    public ResponseEntity<AuthDto.ApiResponse<Boolean>> verifyEmail(@RequestParam("token") String token) {
        boolean verified = authService.verifyEmail(token);
        return ResponseEntity.ok(
                AuthDto.ApiResponse.<Boolean>builder()
                        .success(verified)
                        .message(verified ? "Email verified successfully" : "Email verification failed")
                        .data(verified)
                        .build()
        );
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile")
    public ResponseEntity<AuthDto.ApiResponse<AuthDto.UserResponse>> getCurrentUser(
            @RequestHeader(value = "X-User-Email", required = false) String headerEmail,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : headerEmail;
        AuthDto.UserResponse user = authService.getCurrentUser(email);
        return ResponseEntity.ok(
                AuthDto.ApiResponse.<AuthDto.UserResponse>builder()
                        .success(true)
                        .message("User profile retrieved")
                        .data(user)
                        .build()
        );
    }

    @GetMapping("/business/{businessId}/users")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BUSINESS_OWNER', 'ADMIN')")
    @Operation(summary = "List all team members for a business")
    public ResponseEntity<AuthDto.ApiResponse<List<AuthDto.UserResponse>>> getBusinessUsers(@PathVariable Long businessId) {
        List<AuthDto.UserResponse> users = authService.getUsersByBusiness(businessId);
        return ResponseEntity.ok(
                AuthDto.ApiResponse.<List<AuthDto.UserResponse>>builder()
                        .success(true)
                        .message("Business users retrieved")
                        .data(users)
                        .build()
        );
    }

    @PutMapping("/users/{userId}/role")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BUSINESS_OWNER')")
    @Operation(summary = "Update user role (RBAC)")
    public ResponseEntity<AuthDto.ApiResponse<AuthDto.UserResponse>> updateUserRole(
            @PathVariable Long userId,
            @RequestBody AuthDto.UpdateRoleRequest request) {
        AuthDto.UserResponse updated = authService.updateUserRole(userId, request.getRole());
        return ResponseEntity.ok(
                AuthDto.ApiResponse.<AuthDto.UserResponse>builder()
                        .success(true)
                        .message("Role updated successfully")
                        .data(updated)
                        .build()
        );
    }
}

