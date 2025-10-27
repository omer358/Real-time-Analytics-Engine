package com.example.analyticsservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            Object[] enumConstants = ex.getRequiredType().getEnumConstants();
            String allowedValues = Arrays.stream(enumConstants)
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            Map<String, String> errorResponse = Map.of(
                    "error", "Invalid value for parameter: " + ex.getName(),
                    "message", "Allowed values are: " + allowedValues
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }

        Map<String, String> errorResponse = Map.of(
                "error", "Invalid parameter: " + ex.getName(),
                "message", "Expected type: " + (ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown")
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
