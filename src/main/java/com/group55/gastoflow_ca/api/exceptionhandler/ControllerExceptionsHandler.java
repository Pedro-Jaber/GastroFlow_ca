package com.group55.gastoflow_ca.api.exceptionhandler;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.group55.gastoflow_ca.core.exceptions.UserTypeAlreadyExistsException;

@ControllerAdvice
public class ControllerExceptionsHandler {

    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionsHandler.class);
    private static final String ERROR_BASE_URI = "/errors/";

    // * Spring Exceptions

    // ── Bean Validation (@Valid) ────────────────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        List<String> errors = new ArrayList<>();

        for (var error : e.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }

        logger.error("Validation Error: {}", errors);

        ProblemDetail problem = buildProblem(
                HttpStatus.BAD_REQUEST,
                "validation",
                "Validation Error",
                "One or more fields are invalid");
        problem.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(problem);
    }

    private ProblemDetail buildProblem(HttpStatus status, String errorType,
            String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(URI.create(ERROR_BASE_URI + errorType));
        problem.setTitle(title);
        return problem;
    }

    // * Business Rule Exceptions

    // ── User Type ───────────────────────────────────────────────────────────

    @ExceptionHandler(UserTypeAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleUserTypeAlreadyExistsException(UserTypeAlreadyExistsException e) {
        ProblemDetail problem = buildProblem(HttpStatus.CONFLICT, "user-type-already-exists",
                "User Type Already Exists", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

}