package com.app.LMS.attendanceManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.LMS.attendanceManagement.model.Attendance;
import com.app.LMS.userManagement.model.User;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudentAndOtp_Course_Id(User student, Long courseId);
    boolean existsByOtp_CodeAndStudent_Id(String otpCode, Long studentId);
        List<Attendance> findByOtp_Course_Id(Long courseId);
}
