package com.example.ingestionservice.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
@Builder
public class ErrorResponse {
    private String message;
    private Instant timestamp;
    private int status;
    private Map<String, String> errors;
}
