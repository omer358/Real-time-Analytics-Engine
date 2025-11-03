package com.example.analyticsservice.dto;

public record AnalyticsResponse<T>(String interval, T value) {}
