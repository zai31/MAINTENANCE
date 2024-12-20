package com.app.LMS.assessmentManagement.controller;

import com.app.LMS.DTO.FeedbackRequest;
import com.app.LMS.assessmentManagement.model.Assignment;
import com.app.LMS.assessmentManagement.model.Feedback;
import com.app.LMS.assessmentManagement.model.Submission;
import com.app.LMS.assessmentManagement.service.AssignmentService;
import com.app.LMS.assessmentManagement.service.FeedbackService;
import com.app.LMS.config.JwtConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import com.app.LMS.assessmentManagement.service.SubmissionService;
import java.util.List;

@RestController
@RequestMapping("/api/assignment")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final JwtConfig jwtConfig;
    private final SubmissionService submissionService;
    private final FeedbackService feedbackService;

    public AssignmentController(AssignmentService assignmentService, JwtConfig jwtConfig, SubmissionService submissionService, FeedbackService feedbackService) {
        this.assignmentService = assignmentService;
        this.jwtConfig = jwtConfig;
        this.submissionService = submissionService;
        this.feedbackService = feedbackService;
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createAssignment(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file,
            @RequestParam("courseId") Long courseId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("deadline") String deadline) {

        String role = jwtConfig.getRoleFromToken(token);
        if (!"INSTRUCTOR".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        try {
            // Convert deadline to LocalDateTime
            LocalDateTime parsedDeadline = LocalDateTime.parse(deadline);

            // Create Assignment object
            Assignment assignment = new Assignment();
            assignment.setTitle(title);
            assignment.setDescription(description);
            assignment.setDeadline(parsedDeadline);

            // Create assignment
            assignmentService.createAssignment(assignment, courseId, file);

            return new ResponseEntity<>("Assignment created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating assignment: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> submitSolution(
            @RequestHeader("Authorization") String token,
            @RequestParam("assignmentId") Long assignmentId,
            @RequestParam("file") MultipartFile file) {

        // Validate the student's role
        String role = jwtConfig.getRoleFromToken(token);
        Long studentId = jwtConfig.getUserIdFromToken(token);

        if (!"STUDENT".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        try {
            submissionService.submitSolution(assignmentId, studentId, file);
            return new ResponseEntity<>("Solution submitted successfully!", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error submitting solution: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Assignment>> getAllAssignments(@PathVariable Long courseId) {
        List<Assignment> assignments = assignmentService.getAllAssignments(courseId);
        return new ResponseEntity<>(assignments, HttpStatus.OK);
    }

    @GetMapping("/submission/{assignmentId}")
    public ResponseEntity<?> getAllSubmissions(@RequestHeader("Authorization") String token, @PathVariable Long assignmentId) {
        String role = jwtConfig.getRoleFromToken(token);
        if (!"INSTRUCTOR".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        List<Submission> submissions = submissionService.getAllSubmissions(assignmentId);
        return new ResponseEntity<>(submissions, HttpStatus.OK);
    }

    @GetMapping("/feedback/create")
    public ResponseEntity<String> createFeedback(@RequestHeader("Authorization") String token, @RequestBody FeedbackRequest feedbackRequest) {
        String role = jwtConfig.getRoleFromToken(token);
        if (!"INSTRUCTOR".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        try
        {
            feedbackService.giveFeedback(feedbackRequest);
            return new ResponseEntity<>("Feedback given successfully", HttpStatus.CREATED);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/feedback/{submissionId}")
    public ResponseEntity<?> getFeedback(@RequestHeader("Authorization") String token, @PathVariable Long submissionId) {

        String role = jwtConfig.getRoleFromToken(token);

        // Ensure that only students can access this endpoint
        if (!"STUDENT".equals(role)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }

        try {
            FeedbackRequest feedback = feedbackService.getFeedbackBySubmission(submissionId);
            if (feedback == null) {
                return new ResponseEntity<>("Feedback not found", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(feedback, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
