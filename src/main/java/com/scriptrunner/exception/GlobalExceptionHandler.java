package com.scriptrunner.exception;

import com.scriptrunner.dto.ProblemDetail;
import com.scriptrunner.dto.ProblemType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ResponseEntity<ProblemDetail> handleBadCredentials(BadCredentialsException ex){
        return this.buildProblemDetailResponse(HttpStatus.UNAUTHORIZED, ProblemType.BAD_CREDENTIALS, ex);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity<ProblemDetail> handleInternalServerError(Exception ex) {
        ex.printStackTrace();
        return this.buildProblemDetailResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ProblemType.SERVER_ERROR,
                "An unexpected error occurred. Please try again later."
        );
    }

    private ResponseEntity<ProblemDetail> buildProblemDetailResponse(HttpStatus status, ProblemType problemType, Exception ex){
        ProblemDetail problemDetail = this.createProblemDetail(status, problemType, ex.getMessage());
        return ResponseEntity.status(status).body(problemDetail);
    }

    private ResponseEntity<ProblemDetail> buildProblemDetailResponse(
            HttpStatus status, ProblemType problemType, String detail) {
        ProblemDetail problemDetail = this.createProblemDetail(status, problemType, detail);
        return ResponseEntity.status(status).body(problemDetail);
    }

    private ProblemDetail createProblemDetail(HttpStatus status, ProblemType problemType, String detail){
        return ProblemDetail.builder()
                .status(status.value())
                .type(problemType.getUri())
                .title(problemType.getTitle())
                .detail(detail)
                .build();
    }
}
