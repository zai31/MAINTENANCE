package com.app.LMS.assessmentManagement.controller;

import com.app.LMS.DTO.FeedbackDTO;
import com.app.LMS.DTO.QuizAttemptDTO;
import com.app.LMS.assessmentManagement.service.PerformanceService;
import com.app.LMS.config.JwtConfig;
import com.app.LMS.courseManagement.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assessment")
public class AssessmentController {

    private final PerformanceService performanceService;
    private final JwtConfig jwtConfig;
    private final CourseService courseService;

    public AssessmentController(PerformanceService performanceService, JwtConfig jwtConfig, CourseService courseService) {
        this.performanceService = performanceService;
        this.jwtConfig = jwtConfig;
        this.courseService = courseService;
    }

    @GetMapping("")
    public ResponseEntity<?> getStudentPerformance(@RequestHeader("Authorization") String token,@RequestParam Long studentId, @RequestParam Long courseId) {
        String role = jwtConfig.getRoleFromToken(token);
        Long instructorId = jwtConfig.getUserIdFromToken(token);
        if(!"INSTRUCTOR".equals(role) && !"ADMIN".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }
        if("INSTRUCTOR".equals(role)) {
            if(!courseService.findCourseById(courseId).getInstructor().getId().equals(instructorId)) {
                return new ResponseEntity<>("You are not the owner of this course",HttpStatus.FORBIDDEN);
            }
        }

        List<QuizAttemptDTO> quizAttempts = performanceService.getQuizAttemptsByStudentId(studentId, courseId);
        List<FeedbackDTO> feedbacks = performanceService.getFeedbacksByStudentId(studentId, courseId);

        Map<String, Object> response = new HashMap<>();
        response.put("Quiz attempts", quizAttempts);
        response.put("Feedbacks", feedbacks);

        return ResponseEntity.ok(response);
    }
}
