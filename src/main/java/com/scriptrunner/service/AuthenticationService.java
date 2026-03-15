package com.scriptrunner.service;

import com.scriptrunner.dto.AuthenticationResponseDTO;
import com.scriptrunner.dto.LoginRequestDTO;

public interface AuthenticationService {
    AuthenticationResponseDTO authenticate(LoginRequestDTO credentials);
}
