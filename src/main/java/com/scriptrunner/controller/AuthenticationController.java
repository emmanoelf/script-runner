package com.scriptrunner.controller;

import com.scriptrunner.dto.AuthenticationResponseDTO;
import com.scriptrunner.dto.LoginRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

public interface AuthenticationController {
    @Operation(
            summary = "Authenticate a user",
            description = "Receives username and password, validates credentials, and returns access and refresh tokens.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully authenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponseDTO.class))})
    })
    ResponseEntity<AuthenticationResponseDTO> authenticate(LoginRequestDTO credentials);
}
