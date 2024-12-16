package com.app.LMS.assessmentManagement.controller;

import com.app.LMS.assessmentManagement.model.Submission;
import com.app.LMS.assessmentManagement.service.SubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitAssignment(@RequestBody Submission submission) {
        try {
            submissionService.createSubmission(submission);
            return new ResponseEntity<>("Submission created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating submission: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
