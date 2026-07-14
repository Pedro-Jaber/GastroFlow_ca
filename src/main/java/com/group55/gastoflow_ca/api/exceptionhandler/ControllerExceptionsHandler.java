package com.group55.gastoflow_ca.api.exceptionhandler;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.group55.gastoflow_ca.core.exceptions.ForbiddenActionException;
import com.group55.gastoflow_ca.core.exceptions.InvalidCredentialsException;
import com.group55.gastoflow_ca.core.exceptions.InvalidTokenException;
import com.group55.gastoflow_ca.core.exceptions.MenuItemNotFoundException;
import com.group55.gastoflow_ca.core.exceptions.RestaurantAlreadyExistsException;
import com.group55.gastoflow_ca.core.exceptions.RestaurantNotFoundException;
import com.group55.gastoflow_ca.core.exceptions.UserAlreadyExistsException;
import com.group55.gastoflow_ca.core.exceptions.UserNotFoundException;
import com.group55.gastoflow_ca.core.exceptions.UserTypeAlreadyExistsException;
import com.group55.gastoflow_ca.core.exceptions.UserTypeNotFoundException;

@ControllerAdvice
public class ControllerExceptionsHandler {

    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionsHandler.class);
    private static final String ERROR_BASE_URI = "/errors/";

    // * Spring Exceptions

    // ── Request ─────────────────────────────────────────────────────────────

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        logger.error("Invalid request body", e);
        ProblemDetail problem = buildProblem(
                HttpStatus.BAD_REQUEST,
                "invalid-request-body",
                "Invalid Request Body",
                "The request body is invalid");
        return ResponseEntity.badRequest().body(problem);
    }

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

    // ── Auth ────────────────────────────────────────────────────────────────

    @ExceptionHandler(ForbiddenActionException.class)
    public ResponseEntity<ProblemDetail> handleForbiddenActionException(ForbiddenActionException e) {
        ProblemDetail problem = buildProblem(HttpStatus.FORBIDDEN, "forbidden-action",
                "Forbidden Action", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ProblemDetail> handleInvalidTokenException(InvalidTokenException e) {
        ProblemDetail problem = buildProblem(HttpStatus.UNAUTHORIZED, "invalid-token",
                "Invalid Token", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleInvalidCredentialsException(InvalidCredentialsException e) {
        ProblemDetail problem = buildProblem(HttpStatus.UNAUTHORIZED, "invalid-credentials",
                "Invalid Credentials", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    // ── User Type ───────────────────────────────────────────────────────────

    @ExceptionHandler(UserTypeAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleUserTypeAlreadyExistsException(UserTypeAlreadyExistsException e) {
        ProblemDetail problem = buildProblem(HttpStatus.CONFLICT, "user-type-already-exists",
                "User Type Already Exists", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(UserTypeNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUserTypeNotFoundException(UserTypeNotFoundException e) {
        ProblemDetail problem = buildProblem(HttpStatus.NOT_FOUND, "user-type-not-found",
                "User Type Not Found", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    // ── User ────────────────────────────────────────────────────────────────

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        ProblemDetail problem = buildProblem(HttpStatus.CONFLICT, "user-already-exists",
                "User Already Exists", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUserNotFoundException(UserNotFoundException e) {
        ProblemDetail problem = buildProblem(HttpStatus.NOT_FOUND, "user-not-found",
                "User Not Found", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    // ── Restaurant ──────────────────────────────────────────────────────────

    @ExceptionHandler(RestaurantAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleRestaurantAlreadyExistsException(RestaurantAlreadyExistsException e) {
        ProblemDetail problem = buildProblem(HttpStatus.CONFLICT, "restaurant-already-exists",
                "Restaurant Already Exists", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleRestaurantNotFoundException(RestaurantNotFoundException e) {
        ProblemDetail problem = buildProblem(HttpStatus.NOT_FOUND, "restaurant-not-found",
                "Restaurant Not Found", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    // ── Menu Item ───────────────────────────────────────────────────────────

    @ExceptionHandler(MenuItemNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleMenuItemNotFoundException(MenuItemNotFoundException e) {
        ProblemDetail problem = buildProblem(HttpStatus.NOT_FOUND, "menu-item-not-found",
                "Menu Item Not Found", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    // ── Fallback ────────────────────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpectedException(Exception e) {
        logger.error("Unexpected error", e);

        ProblemDetail problem = buildProblem(HttpStatus.INTERNAL_SERVER_ERROR, "internal-error",
                "Internal Server Error", "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }

}