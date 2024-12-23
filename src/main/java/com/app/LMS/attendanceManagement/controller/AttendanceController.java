package com.app.LMS.attendanceManagement.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.LMS.DTO.StudentInfoDTO;
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

    @PostMapping("/generate-report")
public ResponseEntity<String> generateAttendanceReportWithVisualization(
        @RequestHeader("Authorization") String token,
        @RequestParam Long courseId) {
    try {
        // Validate role (Admin or Instructor)
        String role = jwtConfig.getRoleFromToken(token);
        if (!"ADMIN".equals(role) && !"INSTRUCTOR".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only Admins and Instructors can generate reports.");
        }

        // Retrieve Instructor ID
        Long userId = jwtConfig.getUserIdFromToken(token);

        // Check if the user owns the course (for instructors only)
        Course course = courseService.findCourseById(courseId);
        if ("INSTRUCTOR".equals(role) && !course.getInstructor().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to generate reports for this course.");
        }

        // Retrieve enrolled students and attendance for the course
        List<StudentInfoDTO> enrolledStudents = courseService.getEnrolledStudents(courseId);
        List<Attendance> attendanceList = attendanceService.getAttendanceByCourse(courseId);

        // Define file paths
        String directoryPath = "Performance";
        Path path = Paths.get(directoryPath);
        String excelFilePath = directoryPath + "/Attendance_Report_Course_" + courseId + ".xlsx";
        String chartFilePath = directoryPath + "/Attendance_Chart_Course_" + courseId + ".png";

        // Ensure directory exists
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }

        // Declare lists for chart data
        List<String> studentNames = new ArrayList<>();
        List<Long> attendanceCounts = new ArrayList<>();

        // Generate Excel report
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Attendance Report");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Student ID");
            headerRow.createCell(1).setCellValue("First Name");
            headerRow.createCell(2).setCellValue("Last Name");
            headerRow.createCell(3).setCellValue("Email");
            headerRow.createCell(4).setCellValue("Attendance Count");

            // Populate rows with data
            int rowIndex = 1;

            for (StudentInfoDTO student : enrolledStudents) {
                long attendanceCount = attendanceList.stream()
                        .filter(a -> a.getStudent().getId().equals(student.getStudentId()))
                        .count();

                // Populate Excel row
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(student.getStudentId());
                row.createCell(1).setCellValue(student.getFirstName());
                row.createCell(2).setCellValue(student.getLastName());
                row.createCell(3).setCellValue(student.getEmail());
                row.createCell(4).setCellValue(attendanceCount);

                // Add to chart data
                studentNames.add(student.getFirstName() + " " + student.getLastName());
                attendanceCounts.add(attendanceCount);
            }

            // Save Excel file
            try (FileOutputStream fileOut = new FileOutputStream(excelFilePath)) {
                workbook.write(fileOut);
            }
        }

        // Generate chart using XChart
        CategoryChart chart = new CategoryChartBuilder()
                .width(800)
                .height(600)
                .title("Attendance Chart for Course " + courseId)
                .xAxisTitle("Students")
                .yAxisTitle("Attendance Count")
                .build();

        // Add data series to the chart
        chart.addSeries("Attendance", studentNames, attendanceCounts);

        // Save the chart as an image
        BitmapEncoder.saveBitmap(chart, chartFilePath, BitmapEncoder.BitmapFormat.PNG);

        // Return response with file paths
        return ResponseEntity.ok("Report and chart generated successfully:\n" +
                "Excel Report: " + excelFilePath + "\n" +
                "Chart: " + chartFilePath);
    } catch (IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error generating the report or chart: " + e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unexpected error occurred: " + e.getMessage());
    }
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