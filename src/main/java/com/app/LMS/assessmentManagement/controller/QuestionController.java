package com.app.LMS.assessmentManagement.controller;

import com.app.LMS.assessmentManagement.model.Question;
import com.app.LMS.assessmentManagement.service.QuestionBankService;
import com.app.LMS.assessmentManagement.service.QuestionService;
import com.app.LMS.config.JwtConfig;
import com.app.LMS.courseManagement.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/question")
public class QuestionController {

    private final QuestionService questionService;
    private final QuestionBankService questionBankService;
    private final JwtConfig jwtConfig;
    private final CourseService courseService;

    QuestionController(QuestionService questionService, QuestionBankService questionBankService, JwtConfig jwtConfig, CourseService courseService) {
        this.questionService = questionService;
        this.questionBankService = questionBankService;
        this.jwtConfig = jwtConfig;
        this.courseService = courseService;
    }

    // Create a new question
    @PostMapping("/create")
    public ResponseEntity<?> createAndAddToBank(@RequestHeader("Authorization") String token, @RequestParam Long courseId, @RequestBody @Valid Question question) {
        String role = jwtConfig.getRoleFromToken(token);
        Long instructorId = jwtConfig.getUserIdFromToken(token);

        if (!"INSTRUCTOR".equals(role) && !"ADMIN".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }
        if("INSTRUCTOR".equals(role)) {
            if(!courseService.findCourseById(courseId).getInstructor().getId().equals(instructorId)) {
                return new ResponseEntity<>("Unauthorized: You are not the owner of this course", HttpStatus.FORBIDDEN);
            }
        }

        try {
            // Create question
            Question createdQuestion = questionService.createQuestion(question);

            // Add question to the question bank for the specified course
            questionBankService.addQuestionToBank(courseId, createdQuestion);

            return new ResponseEntity<>(createdQuestion, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getQuestionsByCourse(@RequestHeader("Authorization") String token,@PathVariable Long courseId) {
        String role = jwtConfig.getRoleFromToken(token);
        Long instructorId = jwtConfig.getUserIdFromToken(token);

        if (!"INSTRUCTOR".equals(role) && !"ADMIN".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }
        if("INSTRUCTOR".equals(role)) {
            if(!courseService.findCourseById(courseId).getInstructor().getId().equals(instructorId)) {
                return new ResponseEntity<>("Unauthorized: You are not the owner of this course", HttpStatus.FORBIDDEN);
            }
        }

        List<Question> questions = questionBankService.getQuestionsByCourse(courseId);
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    // Get a question by its ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionById(@RequestHeader("Authorization") String token,@PathVariable Long id) {
        String role = jwtConfig.getRoleFromToken(token);
        if (!"INSTRUCTOR".equals(role) && !"ADMIN".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        Question question = questionService.getQuestionById(id);
        if (question == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(question, HttpStatus.OK);
    }

    // Update an existing question
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateQuestion(@RequestHeader("Authorization") String token,@PathVariable Long id, @RequestBody @Valid Question question) {
        String role = jwtConfig.getRoleFromToken(token);
        if (!"INSTRUCTOR".equals(role) && !"ADMIN".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }
        Question updatedQuestion = questionService.updateQuestion(id, question);
        if (updatedQuestion == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedQuestion, HttpStatus.OK);
    }

    // Delete a question
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteQuestion(@RequestHeader("Authorization") String token,@PathVariable Long id) {
        String role = jwtConfig.getRoleFromToken(token);
        if (!"INSTRUCTOR".equals(role) && !"ADMIN".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        if (questionService.deleteQuestion(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
