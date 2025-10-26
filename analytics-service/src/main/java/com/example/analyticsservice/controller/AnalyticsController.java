package com.example.analyticsservice.controller;

import com.example.analyticsservice.service.PinotQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final PinotQueryService pinotQueryService;

    public AnalyticsController(PinotQueryService pinotQueryService) {
        this.pinotQueryService = pinotQueryService;
    }

    @GetMapping("/orders/count")
    public Map<String, Object> getOrderCount(@RequestParam(defaultValue = "hour") String interval) {
        long count = pinotQueryService.getOrderCount(interval);
        return Map.of("interval", interval, "orderCount", count);
    }

    @GetMapping("/revenue")
    public Map<String, Object> getTotalRevenue(@RequestParam(defaultValue = "hour") String interval) {
        double revenue = pinotQueryService.getTotalRevenue(interval);
        return Map.of("interval", interval, "totalRevenue", revenue);
    }
}
