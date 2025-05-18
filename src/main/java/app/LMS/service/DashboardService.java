package app.LMS.service;

import app.LMS.dto.dashboard.StudentDashboardDTO;
import app.LMS.dto.dashboard.InstructorDashboardDTO;
import app.LMS.dto.dashboard.AdminDashboardDTO;

public interface DashboardService {
    StudentDashboardDTO getStudentDashboard(Long userId);
    InstructorDashboardDTO getInstructorDashboard(Long userId);
    AdminDashboardDTO getAdminDashboard(Long userId);
} 