package com.app.LMS.assessmentManagement.controller;

import com.app.LMS.assessmentManagement.model.Question;
import com.app.LMS.assessmentManagement.service.QuestionBankService;
import com.app.LMS.assessmentManagement.service.QuestionService;
import com.app.LMS.config.JwtConfig;
import com.app.LMS.courseManagement.service.CourseService;
import com.app.LMS.common.Constants;
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
    public ResponseEntity<Question> createAndAddToBank(@RequestHeader("Authorization") String token, @RequestParam Long courseId, @RequestBody @Valid Question question) {
        String role = jwtConfig.getRoleFromToken(token);
        Long instructorId = jwtConfig.getUserIdFromToken(token);

        if (!(Constants.ROLE_INSTRUCTOR.equals(role) || Constants.ROLE_ADMIN.equals(role)) ||
                (Constants.ROLE_INSTRUCTOR.equals(role) && !courseService.findCourseById(courseId).getInstructor().getId().equals(instructorId))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
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
    public ResponseEntity<List<Question>> getQuestionsByCourse(@RequestHeader("Authorization") String token, @PathVariable Long courseId) {
        String role = jwtConfig.getRoleFromToken(token);
        Long instructorId = jwtConfig.getUserIdFromToken(token);

        if (!(Constants.ROLE_INSTRUCTOR.equals(role) || Constants.ROLE_ADMIN.equals(role)) ||
                (Constants.ROLE_INSTRUCTOR.equals(role) && !courseService.findCourseById(courseId).getInstructor().getId().equals(instructorId))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        List<Question> questions = questionBankService.getQuestionsByCourse(courseId);
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    // Get a question by its ID
    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        String role = jwtConfig.getRoleFromToken(token);
        if (!(Constants.ROLE_INSTRUCTOR.equals(role) || Constants.ROLE_ADMIN.equals(role))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Question question = questionService.getQuestionById(id);
        if (question == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(question, HttpStatus.OK);
    }

    // Update an existing question
    @PutMapping("/update/{id}")
    public ResponseEntity<Question> updateQuestion(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody @Valid Question question) {
        String role = jwtConfig.getRoleFromToken(token);
        if (!(Constants.ROLE_INSTRUCTOR.equals(role) || Constants.ROLE_ADMIN.equals(role))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Question updatedQuestion = questionService.updateQuestion(id, question);
        if (updatedQuestion == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedQuestion, HttpStatus.OK);
    }

    // Delete a question
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteQuestion(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        String role = jwtConfig.getRoleFromToken(token);
        if (!(Constants.ROLE_INSTRUCTOR.equals(role) || Constants.ROLE_ADMIN.equals(role))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (questionService.deleteQuestion(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
