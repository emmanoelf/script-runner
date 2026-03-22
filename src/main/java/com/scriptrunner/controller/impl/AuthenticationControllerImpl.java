package com.scriptrunner.controller.impl;

import com.scriptrunner.controller.AuthenticationController;
import com.scriptrunner.dto.AuthenticationResponseDTO;
import com.scriptrunner.dto.LoginRequestDTO;
import com.scriptrunner.security.AuthCookieProvider;
import com.scriptrunner.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthenticationControllerImpl implements AuthenticationController {
    private final AuthenticationService authenticationService;
    private final AuthCookieProvider authCookieProvider;

    @Override
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@RequestBody LoginRequestDTO credentials,
                                                                  HttpServletResponse response) {

            AuthenticationResponseDTO authResponse = this.authenticationService.authenticate(credentials);
            this.authCookieProvider.addAccessTokenCookie(response, authResponse.accessToken());
            return ResponseEntity.status(HttpStatus.OK).body(authResponse);

    }
}
