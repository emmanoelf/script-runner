package com.scriptrunner.dto;

import java.util.UUID;

public record UserJwtPrincipal(
        UUID id,
        String username
) {
}
