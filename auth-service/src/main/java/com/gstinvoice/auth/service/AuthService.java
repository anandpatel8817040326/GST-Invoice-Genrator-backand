package com.gstinvoice.auth.service;

import com.gstinvoice.auth.dto.AuthDto;
import com.gstinvoice.auth.entity.Role;
import com.gstinvoice.auth.entity.User;

import java.util.List;

public interface AuthService {
    AuthDto.AuthResponse register(AuthDto.RegisterRequest request);
    AuthDto.AuthResponse login(AuthDto.LoginRequest request);
    AuthDto.AuthResponse directEmailLogin(String email);
    AuthDto.AuthResponse refreshToken(AuthDto.RefreshTokenRequest request);
    void logout(String email);
    String forgotPassword(AuthDto.ForgotPasswordRequest request);
    void resetPassword(AuthDto.ResetPasswordRequest request);
    boolean verifyEmail(String token);
    AuthDto.UserResponse getCurrentUser(String email);
    List<AuthDto.UserResponse> getUsersByBusiness(Long businessId);
    AuthDto.UserResponse updateUserRole(Long userId, Role newRole);
}

