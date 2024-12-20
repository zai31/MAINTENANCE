package com.app.LMS.assessmentManagement.controller;

import com.app.LMS.DTO.QuizDetailsDTO;
import com.app.LMS.DTO.QuizRequest;
import com.app.LMS.DTO.QuizResponseDTO;
import com.app.LMS.DTO.SubmitQuizRequest;
import com.app.LMS.assessmentManagement.model.Quiz;
import com.app.LMS.assessmentManagement.model.QuizAttempt;
import com.app.LMS.assessmentManagement.service.QuizService;
import com.app.LMS.config.JwtConfig;
import org.jboss.logging.annotations.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;
    private final JwtConfig jwtConfig;

    QuizController(QuizService quizService, JwtConfig jwtConfig) {
        this.quizService = quizService;
        this.jwtConfig = jwtConfig;
    }

    // Create a new Quiz
    @PostMapping("/create")
    public ResponseEntity<?> createQuiz(@RequestHeader("Authorization") String token,@RequestBody @Valid QuizRequest request) {
        String role = jwtConfig.getRoleFromToken(token);
        if (!"INSTRUCTOR".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        Quiz quiz = quizService.createQuiz(request);
        return new ResponseEntity<>("Quiz created successfully with ID: " + quiz.getId(), HttpStatus.CREATED);
    }

    // Update Quiz details
    @PutMapping("update/{id}")
    public ResponseEntity<?> updateQuiz(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody @Valid QuizRequest updatedQuizRequest) {
        String role = jwtConfig.getRoleFromToken(token);
        if (!"INSTRUCTOR".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        Quiz updatedQuiz = quizService.updateQuiz(id, updatedQuizRequest);
        if (updatedQuiz == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Return 404 if quiz not found
        }
        return new ResponseEntity<>(updatedQuiz, HttpStatus.OK);  // Return 200 for successful update
    }

    // Get all quizzes for a specific course
    @GetMapping("/list")
    public ResponseEntity<?> getQuizzesByCourse(@RequestHeader("Authorization") String token, @Param Long courseId) {
        String role = jwtConfig.getRoleFromToken(token);
        if (!"INSTRUCTOR".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        List<QuizResponseDTO> quizzes = quizService.getQuizzesByCourse(courseId);
        if (quizzes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // Return 204 if no quizzes found
        }
        return new ResponseEntity<>(quizzes, HttpStatus.OK);  // Return 200 with quizzes list
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<?> getQuiz(@RequestHeader("Authorization") String token, @PathVariable Long quizId) {
        try {
            // Extract role from token (if necessary)
            String role = jwtConfig.getRoleFromToken(token);

            if(!"STUDENT".equals(role)){
                return ResponseEntity.status(401).body("Unauthorized");
            }
            // Fetch the quiz details from the service
            QuizDetailsDTO quizDetails = quizService.getQuizDetails(quizId);

            if (quizDetails == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Quiz not found");
            }

            return ResponseEntity.ok(quizDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching quiz details: " + e.getMessage());
        }

    }
    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(@RequestHeader("Authorization") String token, @RequestBody SubmitQuizRequest submissionRequest) {
        String role = jwtConfig.getRoleFromToken(token);
        if (!"STUDENT".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }
        Long studentId = jwtConfig.getUserIdFromToken(token);
        submissionRequest.setStudentId(studentId);
        QuizAttempt attempt = quizService.submitQuiz(submissionRequest);
        return ResponseEntity.ok("Score: " + attempt.getScore());
    }
}