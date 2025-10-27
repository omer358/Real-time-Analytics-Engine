package com.example.analyticsservice.controller;

import com.example.analyticsservice.dto.AnalyticsResponse;
import com.example.analyticsservice.dto.Interval;
import com.example.analyticsservice.service.PinotQueryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final PinotQueryService pinotQueryService;

    private static final String INTERVAL_REGEX = "^(minute|hour|day)$";

    public AnalyticsController(PinotQueryService pinotQueryService) {
        this.pinotQueryService = pinotQueryService;
    }

    @GetMapping("/orders/count")
    public ResponseEntity<AnalyticsResponse<Long>> getOrderCount(
            @Valid @RequestParam(name = "interval", defaultValue = "HOUR") Interval interval) {
        long count = pinotQueryService.getOrderCount(interval.name().toLowerCase(Locale.ROOT));
        return ResponseEntity.ok(new AnalyticsResponse<>(interval.name().toLowerCase(Locale.ROOT), count));
    }

    @GetMapping("/orders/revenue")
    public ResponseEntity<AnalyticsResponse<Double>> getTotalRevenue(
            @Valid @RequestParam(name = "interval", defaultValue = "HOUR") Interval interval) {
        double revenue = pinotQueryService.getTotalRevenue(interval.name().toLowerCase(Locale.ROOT));
        return ResponseEntity.ok(new AnalyticsResponse<>(interval.name().toLowerCase(Locale.ROOT), revenue));
    }
}
