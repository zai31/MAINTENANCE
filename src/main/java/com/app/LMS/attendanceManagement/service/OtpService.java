package com.app.LMS.attendanceManagement.service;

import com.app.LMS.courseManagement.repository.CourseRepository;
import org.springframework.stereotype.Service;

import com.app.LMS.attendanceManagement.model.OTP;
import com.app.LMS.courseManagement.model.Course;
import com.app.LMS.attendanceManagement.repository.OtpRepository;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {
    private final OtpRepository otpRepository;
    private final CourseRepository courseRepository;

    public OtpService(OtpRepository otpRepository, CourseRepository courseRepository) {
        this.otpRepository = otpRepository;
        this.courseRepository = courseRepository;
    }

    public OTP generateOtp(Course course) {
        OTP otp = new OTP();

        // Generate a unique OTP code for the course
        String otpCode = generateUniqueOtpForCourse(course);
        otp.setCode(otpCode);

        otp.setExpirationTime(otp.getClassDateTime().plusMinutes(30));
        otp.setCourse(course);

        return otpRepository.save(otp);
    }

    private String generateUniqueOtpForCourse(Course course) {
        Random random = new Random();
        String otpCode;

        do {
            otpCode = String.format("%06d", random.nextInt(999999)); // Generate a random 6-digit code
        } while (otpRepository.existsByCodeAndCourse(otpCode, course)); // Check for uniqueness within the course

        return otpCode;
    }

    public OTP findByCodeAndCourse(String code, Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId)); // Retrieve course by ID
        if (course == null) {
            return null; // If course does not exist, return null
        }
        return otpRepository.findByCodeAndCourse(code, course);
    }

}
