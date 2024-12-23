package com.app.LMS.attendanceManagement.repository;

import com.app.LMS.attendanceManagement.model.Attendance;
import com.app.LMS.userManagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudentAndOtp_Course_Id(User student, Long courseId);
    boolean existsByOtp_CodeAndStudent_Id(String otpCode, Long studentId);
}
