package com.scriptrunner.dto;

public record AuthenticationResponseDTO(
        String accessToken,
        String refreshToken
) {
}
