package com.app.LMS.assessmentManagement.controller;

import com.app.LMS.DTO.FeedbackRequest;
import com.app.LMS.assessmentManagement.model.Feedback;
import com.app.LMS.assessmentManagement.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addFeedback(@RequestBody FeedbackRequest feedback) {
        try {
            feedbackService.giveFeedback(feedback);
            return new ResponseEntity<>("Feedback added successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error adding feedback: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint to get feedback by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getFeedbackById(@PathVariable Long id) {
        try {
            Feedback feedback = feedbackService.getFeedbackById(id);
            return new ResponseEntity<>(feedback, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Feedback not found: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
