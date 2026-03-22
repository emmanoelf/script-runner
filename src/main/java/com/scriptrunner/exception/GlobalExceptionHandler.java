package com.scriptrunner.exception;

import com.scriptrunner.dto.ProblemDetail;
import com.scriptrunner.dto.ProblemType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler extends BaseExceptionHandler{

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
}
