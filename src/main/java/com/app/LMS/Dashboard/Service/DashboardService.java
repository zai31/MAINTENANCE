package com.app.LMS.Dashboard.Service;

import com.app.LMS.DTO.StudentDashboardDTO;
import com.app.LMS.DTO.InstructorDashboardDTO;
import com.app.LMS.DTO.AdminDashboardDTO;

public interface DashboardService {
    StudentDashboardDTO getStudentDashboard(Long userId);
    InstructorDashboardDTO getInstructorDashboard(Long userId);
    AdminDashboardDTO getAdminDashboard(Long userId);
} 