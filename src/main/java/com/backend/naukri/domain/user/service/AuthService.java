package com.backend.naukri.domain.user.service;

import com.backend.naukri.common.enums.Role;
import com.backend.naukri.domain.candidate.service.CandidateProfileService;
import com.backend.naukri.domain.user.dto.AuthResponse;
import com.backend.naukri.domain.user.dto.LoginRequest;
import com.backend.naukri.domain.user.dto.RegisterRequest;
import com.backend.naukri.domain.user.entity.User;
import com.backend.naukri.domain.user.repository.UserRepository;
import com.backend.naukri.exception.BadRequestException;
import com.backend.naukri.exception.ConflictException;
import com.backend.naukri.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles user registration and authentication.
 * Passwords are BCrypt-hashed. JWT is issued on success.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CandidateProfileService candidateProfileService;

    /**
     * Registers a new user. For SEEKER role, an empty candidate profile is
     * auto-created so they can start filling in details immediately.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);

        if (request.getRole() == Role.SEEKER) {
            candidateProfileService.createEmptyProfile(user);
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getRole().name(), user.getEmail());
    }

    /**
     * Validates credentials and returns a JWT access token.
     * Returns the same error message for wrong email or wrong password
     * to avoid revealing which one is incorrect (security best practice).
     */
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!user.isActive()) {
            throw new BadRequestException("Account is deactivated");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getRole().name(), user.getEmail());
    }
}
