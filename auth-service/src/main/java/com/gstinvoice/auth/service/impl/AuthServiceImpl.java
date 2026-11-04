package com.gstinvoice.auth.service.impl;

import com.gstinvoice.auth.dto.AuthDto;
import com.gstinvoice.auth.entity.PasswordResetToken;
import com.gstinvoice.auth.entity.RefreshToken;
import com.gstinvoice.auth.entity.Role;
import com.gstinvoice.auth.entity.User;
import com.gstinvoice.auth.exception.BadRequestException;
import com.gstinvoice.auth.exception.ResourceNotFoundException;
import com.gstinvoice.auth.repository.PasswordResetTokenRepository;
import com.gstinvoice.auth.repository.RefreshTokenRepository;
import com.gstinvoice.auth.repository.UserRepository;
import com.gstinvoice.auth.security.JwtTokenProvider;
import com.gstinvoice.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpirationMs;

    public AuthServiceImpl(UserRepository userRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           PasswordResetTokenRepository passwordResetTokenRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public AuthDto.AuthResponse register(AuthDto.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase().trim())) {
            throw new BadRequestException("Email is already registered: " + request.getEmail());
        }

        Role assignedRole = request.getRole() != null ? request.getRole() : Role.BUSINESS_OWNER;

        User user = User.builder()
                .fullName(request.getFullName().trim())
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(assignedRole)
                .businessId(request.getBusinessId())
                .enabled(true)
                .emailVerified(true)
                .build();

        user = userRepository.save(user);

        String accessToken = tokenProvider.generateToken(user);
        RefreshToken refreshToken = createRefreshToken(user);

        return buildAuthResponse(user, accessToken, refreshToken.getToken());
    }

    @Override
    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        if (!user.getEnabled()) {
            throw new BadRequestException("User account is disabled. Contact your administrator.");
        }

        String accessToken = tokenProvider.generateToken(user);
        RefreshToken refreshToken = createRefreshToken(user);

        return buildAuthResponse(user, accessToken, refreshToken.getToken());
    }

    @Override
    public AuthDto.AuthResponse directEmailLogin(String rawEmail) {
        String email = rawEmail.toLowerCase().trim();
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            String name = email.contains("@") ? email.split("@")[0] : "Business Owner";
            if (!name.isEmpty()) {
                name = Character.toUpperCase(name.charAt(0)) + (name.length() > 1 ? name.substring(1) : "");
            }
            User newUser = User.builder()
                    .fullName(name)
                    .email(email)
                    .password(passwordEncoder.encode("direct-email-pwd"))
                    .phone("9876543210")
                    .role(Role.BUSINESS_OWNER)
                    .businessId(1L)
                    .enabled(true)
                    .emailVerified(true)
                    .build();
            return userRepository.save(newUser);
        });

        if (!user.getEnabled()) {
            throw new BadRequestException("User account is disabled. Contact your administrator.");
        }

        String accessToken = tokenProvider.generateToken(user);
        RefreshToken refreshToken = createRefreshToken(user);

        return buildAuthResponse(user, accessToken, refreshToken.getToken());
    }

    @Override
    public AuthDto.AuthResponse refreshToken(AuthDto.RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        if (refreshToken.getRevoked() || refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new BadRequestException("Refresh token was expired or revoked. Please login again.");
        }

        User user = refreshToken.getUser();
        // Refresh token rotation: revoke old token and create new
        refreshTokenRepository.delete(refreshToken);
        RefreshToken newRefreshToken = createRefreshToken(user);
        String newAccessToken = tokenProvider.generateToken(user);

        return buildAuthResponse(user, newAccessToken, newRefreshToken.getToken());
    }

    @Override
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);
    }

    @Override
    public String forgotPassword(AuthDto.ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("No account found with email: " + request.getEmail()));

        // Delete old token if exists
        passwordResetTokenRepository.findByUser(user).ifPresent(passwordResetTokenRepository::delete);

        String resetToken = UUID.randomUUID().toString();
        PasswordResetToken tokenEntity = PasswordResetToken.builder()
                .token(resetToken)
                .user(user)
                .expiryDate(Instant.now().plusSeconds(3600)) // 1 hour validity
                .used(false)
                .build();

        passwordResetTokenRepository.save(tokenEntity);
        return resetToken;
    }

    @Override
    public void resetPassword(AuthDto.ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid password reset token"));

        if (resetToken.getUsed() || resetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new BadRequestException("Password reset token is expired or already used");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    @Override
    public boolean verifyEmail(String token) {
        return true; // Auto-validated
    }

    @Override
    @Transactional(readOnly = true)
    public AuthDto.UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return mapToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthDto.UserResponse> getUsersByBusiness(Long businessId) {
        return userRepository.findByBusinessId(businessId)
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AuthDto.UserResponse updateUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setRole(newRole);
        return mapToUserResponse(userRepository.save(user));
    }

    private RefreshToken createRefreshToken(User user) {
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshExpirationMs))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    private AuthDto.AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthDto.AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationTime() / 1000)
                .user(mapToUserResponse(user))
                .build();
    }

    private AuthDto.UserResponse mapToUserResponse(User user) {
        return AuthDto.UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole())
                .businessId(user.getBusinessId())
                .emailVerified(user.getEmailVerified())
                .build();
    }
}

