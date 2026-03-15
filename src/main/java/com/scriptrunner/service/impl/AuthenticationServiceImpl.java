package com.scriptrunner.service.impl;

import com.scriptrunner.dto.AuthenticationResponseDTO;
import com.scriptrunner.dto.LoginRequestDTO;
import com.scriptrunner.model.User;
import com.scriptrunner.reporitory.UserRepository;
import com.scriptrunner.security.JwtTokenProvider;
import com.scriptrunner.service.AuthenticationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthenticationResponseDTO authenticate(LoginRequestDTO credentials) {
        User user = this.userRepository.findByUsername(credentials.username()).orElseThrow(() ->
                new EntityNotFoundException("User not found"));

        if(!passwordEncoder.matches(credentials.password(), user.getPasswordHash())){
            throw new BadCredentialsException("Invalid credentials");
        }

        String accessToken = this.jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername(), Collections.emptyMap());
        String refreshToken = this.jwtTokenProvider.generateRefreshTokenFromAccess(accessToken);

        return new AuthenticationResponseDTO(accessToken, refreshToken);
    }
}
