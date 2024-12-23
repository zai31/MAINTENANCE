package com.app.LMS.attendanceManagement.controller;

import com.app.LMS.attendanceManagement.model.Attendance;
import com.app.LMS.attendanceManagement.model.OTP;
import com.app.LMS.attendanceManagement.service.AttendanceService;
import com.app.LMS.attendanceManagement.service.OtpService;
import com.app.LMS.config.JwtConfig;
import com.app.LMS.courseManagement.model.Course;
import com.app.LMS.courseManagement.service.CourseService;
import com.app.LMS.userManagement.model.User;
import com.app.LMS.userManagement.repository.UserRepository;
import com.app.LMS.userManagement.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final OtpService otpService;
    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;
    private final UserService userService;
    private final CourseService courseService;

    public AttendanceController(AttendanceService attendanceService, OtpService otpService, JwtConfig jwtConfig, UserRepository userRepository, UserService userService, CourseService courseService) {
        this.attendanceService = attendanceService;
        this.otpService = otpService;
        this.jwtConfig = jwtConfig;
        this.userRepository = userRepository;
        this.userService = userService;
        this.courseService = courseService;
    }

    @PostMapping("/mark")
    public ResponseEntity<String> markAttendance(@RequestHeader("Authorization") String token, @RequestParam String otpCode, @RequestParam Long courseId) {
        // Validate student's role
        String role = jwtConfig.getRoleFromToken(token);
        if (!"STUDENT".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Retrieve student
        Long studentID = jwtConfig.getUserIdFromToken(token);
        User student = userService.findById(studentID).orElseThrow(() -> new RuntimeException("User not found with ID: " + studentID));
        // Find OTP by code and course
        OTP otp = otpService.findByCodeAndCourse(otpCode, courseId);
        if (otp == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP for the specified course.");
        }

        Course course = otp.getCourse();
        boolean enrolled = courseService.isEnrolled(course.getId(), studentID);
        if(!enrolled){
            return ResponseEntity.status(403).body("You must be enrolled in the course to be able to view its content");
        }

        boolean marked = attendanceService.isMarked(otp.getCode(), studentID);
        if(marked){
            return new ResponseEntity<>("error: You have already attended this class", HttpStatus.BAD_REQUEST);
        }
        // Mark attendance
        Boolean response = attendanceService.markAttendance(student, otp);
        if (!response) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP");
        }

        return ResponseEntity.ok("Attendance marked successfully.");
    }

    @GetMapping("/track")
    public ResponseEntity<?> getStudentAttendanceForCourse(@RequestHeader("Authorization") String token, @RequestParam Long studentId, @RequestParam Long courseId) {

        // Validate the role
        String role = jwtConfig.getRoleFromToken(token);
        Long instructorId = jwtConfig.getUserIdFromToken(token);

        if (!"ADMIN".equals(role) && !"INSTRUCTOR".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied.");
        }
        Course course = courseService.findCourseById(courseId);
        if ("INSTRUCTOR".equals(role)) {
            if (!course.getInstructor().getId().equals(instructorId)) {
                return new ResponseEntity<>("Unauthorized: You do not own this course", HttpStatus.FORBIDDEN);
            }
        }
        // Validate if the student exists
        User student = userRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        // Fetch attendance for the student and course
        List<Attendance> attendanceList = attendanceService.getAttendanceByStudentAndCourse(student, courseId);

        if (attendanceList.isEmpty()) {
            return ResponseEntity.ok("No attendance records found for the student in the specified course.");
        }

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("Student ID", studentId);
        responseBody.put("Course ID", courseId);
        responseBody.put("Classes Attended", attendanceList.size());
        responseBody.put("Attended Classes Dates", attendanceList.stream().map(attendance -> attendance.getOtp().getClassDateTime()).toList());

        return ResponseEntity.ok(responseBody);
    }
}