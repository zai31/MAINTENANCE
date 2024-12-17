package com.app.LMS.attendanceManagement.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.app.LMS.attendanceManagement.model.OTP;
import com.app.LMS.courseManagement.model.Course;

public interface OtpRepository extends JpaRepository<OTP, Long> {
    boolean existsByCodeAndCourse(String code, Course course); // Method to check uniqueness within a course
    OTP findByCodeAndCourse(String code, Course course);
}
