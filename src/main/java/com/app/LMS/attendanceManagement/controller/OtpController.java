package com.app.LMS.attendanceManagement.controller;

import com.app.LMS.attendanceManagement.model.OTP;
import com.app.LMS.attendanceManagement.service.OtpService;
import com.app.LMS.config.JwtConfig;
import com.app.LMS.courseManagement.model.Course;
import com.app.LMS.courseManagement.repository.CourseRepository;
import com.app.LMS.courseManagement.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    private final OtpService otpService;
    private final JwtConfig jwtConfig;
    private final CourseRepository courseRepository;

    public OtpController(OtpService otpService, CourseService courseService, JwtConfig jwtConfig, CourseRepository courseRepository) {
        this.otpService = otpService;
        this.jwtConfig = jwtConfig;
        this.courseRepository = courseRepository;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateOtp(@RequestHeader("Authorization") String token, @RequestParam Long courseId)
    {
        // Validate the instructor's role
        String role = jwtConfig.getRoleFromToken(token);
        if (!"INSTRUCTOR".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));;
        OTP otp = otpService.generateOtp(course);

        return ResponseEntity.status(HttpStatus.CREATED).body(otp.getCode());
    }
}
