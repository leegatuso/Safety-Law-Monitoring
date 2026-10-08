package com.company.safetylaw.controller;

import com.company.safetylaw.domain.*;
import com.company.safetylaw.dto.*;
import com.company.safetylaw.service.*;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class DashboardApiController {

    private final DashboardService dashboard;

    public DashboardApiController(DashboardService dashboard) {
        this.dashboard = dashboard;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @GetMapping("/dashboard/summary")
    public Map<String, Object> summary() {
        return dashboard.summary();
    }
}
