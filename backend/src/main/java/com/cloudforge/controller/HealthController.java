package com.cloudforge.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloudforge.dto.ApiResponse;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public ApiResponse<Map<String, String>> health() {

        Map<String, String> healthData = new HashMap<>();

        healthData.put("status", "UP");
        healthData.put("service", "CloudForge Backend");

        return new ApiResponse<>(
                true,
                "Service is running",
                healthData
        );
    }
}