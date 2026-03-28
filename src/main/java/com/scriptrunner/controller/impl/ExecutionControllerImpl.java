package com.scriptrunner.controller.impl;

import com.scriptrunner.dto.ExecutionRequest;
import com.scriptrunner.dto.UserJwtPrincipal;
import com.scriptrunner.service.impl.ExecutionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/executions")
@RequiredArgsConstructor
public class ExecutionControllerImpl {
    private final ExecutionServiceImpl executionService;

    @PostMapping
    public ResponseEntity<UUID> startExecution(@RequestBody ExecutionRequest request, Authentication authentication){
        UserJwtPrincipal user = (UserJwtPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok().body(this.executionService.startExecution(request, user.id()));
    }
}
