package com.app.LMS.assessmentManagement.controller;

import java.util.List;

import com.app.LMS.courseManagement.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.LMS.DTO.QuizDetailsDTO;
import com.app.LMS.DTO.QuizRequest;
import com.app.LMS.DTO.QuizResponseDTO;
import com.app.LMS.DTO.SubmitQuizRequest;
import com.app.LMS.assessmentManagement.model.Quiz;
import com.app.LMS.assessmentManagement.model.QuizAttempt;
import com.app.LMS.assessmentManagement.service.QuizService;
import com.app.LMS.config.JwtConfig;
import com.app.LMS.notificationManagement.eventBus.EventBus;
import com.app.LMS.notificationManagement.eventBus.events.QuizCreatedEvent;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;
    private final JwtConfig jwtConfig;
    private final EventBus eventBus;
    private final CourseService courseService;

    QuizController(QuizService quizService, JwtConfig jwtConfig, EventBus eventBus, CourseService courseService) {
        this.quizService = quizService;
        this.jwtConfig = jwtConfig;
        this.eventBus = eventBus;
        this.courseService = courseService;
    }

    // Create a new Quiz
    @PostMapping("/create")
    public ResponseEntity<?> createQuiz(@RequestHeader("Authorization") String token, @RequestBody @Valid QuizRequest request) {
        String role = jwtConfig.getRoleFromToken(token);
        Long instructorId = jwtConfig.getUserIdFromToken(token);

        if (!"INSTRUCTOR".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }
        if(!courseService.findCourseById(request.getCourseID()).getInstructor().getId().equals(instructorId)){
            return new ResponseEntity<>("Unauthorized: You are not the owner of this course", HttpStatus.FORBIDDEN);
        }

        Quiz quiz = quizService.createQuiz(request);
        QuizCreatedEvent event = new QuizCreatedEvent(quiz.getId());
        eventBus.publish(event);

        return new ResponseEntity<>("Quiz created successfully with ID: " + quiz.getId(), HttpStatus.CREATED);
    }

    // Update Quiz details
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateQuiz(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody @Valid QuizRequest updatedQuizRequest) {
        String role = jwtConfig.getRoleFromToken(token);
        Long instructorId = jwtConfig.getUserIdFromToken(token);

        if (!"INSTRUCTOR".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }
        if(!courseService.findCourseById(updatedQuizRequest.getCourseID()).getInstructor().getId().equals(instructorId)){
            return new ResponseEntity<>("Unauthorized: You are not the owner of this course", HttpStatus.FORBIDDEN);
        }

        Quiz updatedQuiz = quizService.updateQuiz(id, updatedQuizRequest);
        if (updatedQuiz == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedQuiz, HttpStatus.OK);
    }

    // Get all quizzes for a specific course
    @GetMapping("/list")
    public ResponseEntity<?> getQuizzesByCourse(@RequestHeader("Authorization") String token, @RequestParam Long courseId) {
        String role = jwtConfig.getRoleFromToken(token);
        Long instructorId = jwtConfig.getUserIdFromToken(token);

        if (!"INSTRUCTOR".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }
        if(!courseService.findCourseById(courseId).getInstructor().getId().equals(instructorId)){
            return new ResponseEntity<>("Unauthorized: You are not the owner of this course", HttpStatus.FORBIDDEN);
        }

        List<QuizResponseDTO> quizzes = quizService.getQuizzesByCourse(courseId);
        if (quizzes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(quizzes, HttpStatus.OK);
    }

    // Get quiz details by ID
    @GetMapping("/{quizId}")
    public ResponseEntity<?> getQuiz(@RequestHeader("Authorization") String token, @PathVariable Long quizId) {
        try {
            String role = jwtConfig.getRoleFromToken(token);
            Long studentId = jwtConfig.getUserIdFromToken(token);

            if (!"STUDENT".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");
            }
            boolean enrolled = courseService.isEnrolled(quizService.getById(quizId).getCourse().getId(), studentId);
            if(!enrolled){
                return ResponseEntity.status(403).body("You must be enrolled in the course to be able to view its content");
            }

            QuizDetailsDTO quizDetails = quizService.getQuizDetails(quizId);

            if (quizDetails == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Quiz not found");
            }

            return ResponseEntity.ok(quizDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching quiz details: " + e.getMessage());
        }
    }

    // Submit a quiz
    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(@RequestHeader("Authorization") String token, @RequestBody SubmitQuizRequest submissionRequest) {
        String role = jwtConfig.getRoleFromToken(token);
        Long studentId = jwtConfig.getUserIdFromToken(token);

        if (!"STUDENT".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }
        boolean enrolled = courseService.isEnrolled(quizService.getById(submissionRequest.getQuizId()).getCourse().getId(), studentId);
        if(!enrolled){
            return ResponseEntity.status(403).body("You must be enrolled in the course to be able to view its content");
        }
        submissionRequest.setStudentId(studentId);
        try {
            QuizAttempt attempt = quizService.submitQuiz(submissionRequest);
            return ResponseEntity.ok("Score: " + attempt.getScore());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error: " + e.getMessage());
        }
    }
}
