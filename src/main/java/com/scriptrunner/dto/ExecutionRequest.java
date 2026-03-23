package com.scriptrunner.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record ExecutionRequest(
        UUID userId,
        @NotBlank(message = "Instructions must not be blank")
        String instructions,
        String precondition,
        String image
) {
}
