package com.scriptrunner.exception;

import com.scriptrunner.dto.ProblemDetail;
import com.scriptrunner.dto.ProblemType;
import com.scriptrunner.security.AuthCookieProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class AuthExceptionHandler extends BaseExceptionHandler {

    private final AuthCookieProvider authCookieProvider;

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<ProblemDetail> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletResponse response
    ) {
        authCookieProvider.clearAccessTokenCookie(response);
        return this.buildProblemDetailResponse(HttpStatus.UNAUTHORIZED, ProblemType.BAD_CREDENTIALS, ex);
    }
}
