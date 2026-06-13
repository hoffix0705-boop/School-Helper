package com.campushelper.controller;

import com.campushelper.common.R;
import com.campushelper.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public R<Map<String, Object>> stats() {
        return R.ok(dashboardService.getStats());
    }

    @GetMapping("/recent-tasks")
    public R<?> recentTasks() {
        return R.ok(dashboardService.getRecentTasks(5));
    }
}