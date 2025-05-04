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
    private final CourseService courseService;

    public OtpController(OtpService otpService, CourseService courseService, JwtConfig jwtConfig) {
        this.otpService = otpService;
        this.jwtConfig = jwtConfig;
        this.courseService = courseService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateOtp(@RequestHeader("Authorization") String token, @RequestParam Long courseId)
    {
        // Validate the instructor's role
        String role = jwtConfig.getRoleFromToken(token);
        Long instructorId = jwtConfig.getUserIdFromToken(token);

        if (!"INSTRUCTOR".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Course course = courseService.findCourseById(courseId);
        if(!course.getInstructor().getId().equals(instructorId)){
            return new ResponseEntity<>("Unauthorized: You do not own this course", HttpStatus.FORBIDDEN);
        }

        OTP otp = otpService.generateOtp(course);

        return ResponseEntity.status(HttpStatus.CREATED).body("OTP: " + otp.getCode());
    }
}
