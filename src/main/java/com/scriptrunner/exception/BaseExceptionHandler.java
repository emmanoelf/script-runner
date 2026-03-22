package com.scriptrunner.exception;

import com.scriptrunner.dto.ProblemDetail;
import com.scriptrunner.dto.ProblemType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class BaseExceptionHandler {
    protected ResponseEntity<ProblemDetail> buildProblemDetailResponse(
            HttpStatus status, ProblemType problemType, Exception ex) {
        return buildProblemDetailResponse(status, problemType, ex.getMessage());
    }

    protected ResponseEntity<ProblemDetail> buildProblemDetailResponse(
            HttpStatus status, ProblemType problemType, String detail) {
        ProblemDetail problemDetail = ProblemDetail.builder()
                .status(status.value())
                .type(problemType.getUri())
                .title(problemType.getTitle())
                .detail(detail)
                .build();
        return ResponseEntity.status(status).body(problemDetail);
    }
}