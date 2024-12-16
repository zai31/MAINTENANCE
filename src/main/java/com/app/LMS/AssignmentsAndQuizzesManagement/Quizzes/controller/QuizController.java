package com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.controller;


import com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.service.QuizService;
import com.app.LMS.DTO.QuizRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/course")
public class QuizController {

    private final QuizService quizService;
    public QuizController(QuizService quizService) {

        this.quizService = quizService;
    }

    @PostMapping("/{courseId}/quizzes/addQuiz")
    public ResponseEntity<String> addQuiz( @RequestHeader("Authorization") String token,@PathVariable Long courseId,@RequestBody QuizRequest quizRequest ) {
        try {
            ResponseEntity<String> error = quizService.validation(token,courseId,quizRequest.getQuestions());
            if (error == null)
            {
                 return quizService.createQuiz(courseId,quizRequest);
            }
            else {
                return error;
            }
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/{courseId}/quizzes/{quizId}")
    public ResponseEntity<String> editQuiz(@RequestHeader("Authorization") String token,@PathVariable Long courseId,@RequestBody QuizRequest quizRequest, @PathVariable Long quizId) {
        try {
            ResponseEntity<String> error = quizService.validation(token,courseId,quizId,quizRequest.getQuestions());
            if (error == null)
            {
                return quizService.editQuiz(quizId, quizRequest);
            }else
            {
                return error;
            }
        }
        catch (Exception e) {
//            return new ResponseEntity<>("Error Editing Quiz", HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/{courseId}/quizzes/{quizId}")
    public ResponseEntity<String> deleteQuiz(@RequestHeader("Authorization") String token,@PathVariable Long courseId, @PathVariable Long quizId) {
        try{
            ResponseEntity<String> error = quizService.validation(token,courseId,quizId);
            if (error == null)
            {
                return quizService.deleteQuiz(quizId);
            }else
            {
                return error;
            }
        }
        catch (Exception e) {
//            return new ResponseEntity<>("Error Editing Quiz", HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
