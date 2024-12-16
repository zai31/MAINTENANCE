package com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.controller;

import com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.service.QuizBankService;
import com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.service.QuizService;
import com.app.LMS.DTO.QuizBankRequest;
import com.app.LMS.DTO.QuizFromBankRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/course")
public class QuizBankController {
    private final QuizBankService quizBankService;

    public QuizBankController(QuizBankService quizBankService)
    {
        this.quizBankService = quizBankService;
    }
    @PostMapping("/{courseId}/quizzes/quizBank/createQuizBank")
    public ResponseEntity<String> createQuizBank(@RequestHeader("Authorization") String token, @PathVariable Long courseId, @RequestBody QuizBankRequest quizBangRequest ) {
        try {
            ResponseEntity<String> error = quizBankService.validation(token,courseId,quizBangRequest.getQuestions());
            if (error == null)
            {
                return quizBankService.createQuizBank(courseId,quizBangRequest);
            }
            else {
                return error;
            }
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/{courseId}/quizzes/quizBank/{quizBankId}")
    public ResponseEntity<String> editQuizBank(@RequestHeader("Authorization") String token, @PathVariable Long courseId, @RequestBody QuizBankRequest quizBangRequest ,@PathVariable Long quizBankId) {
        try {
            ResponseEntity<String> error = quizBankService.validation(token,courseId,quizBankId,quizBangRequest.getQuestions());
            if (error == null)
            {
                return quizBankService.editQuizBank(quizBankId,quizBangRequest);
            }
            else {
                return error;
            }
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/{courseId}/quizzes/quizBank/{quizBankId}")
    public ResponseEntity<String> deleteQuizBank(@RequestHeader("Authorization") String token, @PathVariable Long courseId,@PathVariable Long quizBankId) {
        try
        {
            ResponseEntity<String> error = quizBankService.validation(token,courseId,quizBankId);
            if (error == null)
            {
                return quizBankService.deleteQuizBank(quizBankId);
            }
            else {
                return error;
            }
        }catch (Exception e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/{courseId}/quizzes/quizBank/{quizBankId}/createQuiz")
    public ResponseEntity<String> createQuizFromQuizBank(@RequestHeader("Authorization") String token, @PathVariable Long courseId, @PathVariable Long quizBankId,@RequestBody QuizFromBankRequest request) {
        try
        {
            ResponseEntity<String> error = quizBankService.validation(token,courseId,quizBankId);
            if (error == null)
            {
                return quizBankService.createQuizFromBankQuiz(request,courseId,quizBankId);
            }
            else {
                return error;
            }
        }catch (Exception e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
