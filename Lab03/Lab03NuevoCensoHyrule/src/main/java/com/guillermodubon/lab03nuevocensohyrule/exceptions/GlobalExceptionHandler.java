package com.guillermodubon.lab03nuevocensohyrule.exceptions;

import com.guillermodubon.lab03nuevocensohyrule.dto.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                exception.getMessage(),
                HttpStatus.NOT_FOUND,
                getRequestUri(request),
                List.of(exception.getMessage())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return buildErrorResponse(
                "Invalid request payload",
                HttpStatus.BAD_REQUEST,
                getRequestUri(request),
                errors
        );
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            PropertyReferenceException.class,
            InvalidDataAccessApiUsageException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequestException(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                "Invalid request parameters",
                HttpStatus.BAD_REQUEST,
                getRequestUri(request),
                List.of(exception.getMessage())
        );
    }

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(
            String message,
            HttpStatus status,
            String path,
            List<String> errors
    ) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .message(message)
                .status(status.value())
                .timestamp(LocalDateTime.now())
                .path(path)
                .errors(errors)
                .build();

        return ResponseEntity.status(status).body(response);
    }

    private String getRequestUri(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString == null || queryString.isBlank()) {
            return request.getRequestURI();
        }

        return request.getRequestURI() + "?" + queryString;
    }
}
