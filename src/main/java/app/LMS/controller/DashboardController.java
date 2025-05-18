package app.LMS.controller;

import app.LMS.dto.dashboard.StudentDashboardDTO;
import app.LMS.dto.dashboard.InstructorDashboardDTO;
import app.LMS.dto.dashboard.AdminDashboardDTO;
import app.LMS.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/student")
    public ResponseEntity<StudentDashboardDTO> getStudentDashboard(Authentication authentication) {
        // Extract user ID from authentication
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(dashboardService.getStudentDashboard(userId));
    }

    @GetMapping("/instructor")
    public ResponseEntity<InstructorDashboardDTO> getInstructorDashboard(Authentication authentication) {
        // Extract user ID from authentication
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(dashboardService.getInstructorDashboard(userId));
    }

    @GetMapping("/admin")
    public ResponseEntity<AdminDashboardDTO> getAdminDashboard(Authentication authentication) {
        // Extract user ID from authentication
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(dashboardService.getAdminDashboard(userId));
    }
} 