package com.app.LMS.assessmentManagement.controller;

import com.app.LMS.DTO.FeedbackRequest;
import com.app.LMS.common.Constants;
import com.app.LMS.assessmentManagement.model.Assignment;
import com.app.LMS.assessmentManagement.model.Feedback;
import com.app.LMS.assessmentManagement.model.Submission;
import com.app.LMS.assessmentManagement.service.AssignmentService;
import com.app.LMS.assessmentManagement.service.FeedbackService;
import com.app.LMS.config.JwtConfig;
import com.app.LMS.courseManagement.model.Course;
import com.app.LMS.courseManagement.service.CourseService;
import com.app.LMS.notificationManagement.eventBus.EventBus;
import com.app.LMS.notificationManagement.eventBus.events.AssignmentCreatedEvent;
import com.app.LMS.notificationManagement.eventBus.events.FeedbackCreatedEvent;
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
@org.springframework.context.annotation.Lazy
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final JwtConfig jwtConfig;
    private final SubmissionService submissionService;
    private final FeedbackService feedbackService;
    private final EventBus eventBus;
    private final CourseService courseService;

    public AssignmentController(AssignmentService assignmentService, JwtConfig jwtConfig, SubmissionService submissionService, FeedbackService feedbackService, @org.springframework.context.annotation.Lazy EventBus eventBus, CourseService courseService) {
        this.assignmentService = assignmentService;
        this.jwtConfig = jwtConfig;
        this.submissionService = submissionService;
        this.feedbackService = feedbackService;
        this.courseService = courseService;
        this.eventBus = eventBus;
    }
    @PostMapping("/test-deadline-reminder")
    public ResponseEntity<String> testDeadlineReminder(@RequestHeader("Authorization") String token) {
        String role = jwtConfig.getRoleFromToken(token);
        if (!Constants.ROLE_INSTRUCTOR.equals(role)) {
            return new ResponseEntity<>(Constants.UNAUTHORIZED, HttpStatus.FORBIDDEN);
        }

        try {
            assignmentService.checkAssignmentDeadlines();
            return new ResponseEntity<>("Deadline reminder check triggered successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error triggering deadline reminder: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
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
        Long instructorId = jwtConfig.getUserIdFromToken(token);
        if (!Constants.ROLE_INSTRUCTOR.equals(role)) {
            return new ResponseEntity<>(Constants.UNAUTHORIZED, HttpStatus.FORBIDDEN);
        }
        Course course = courseService.findCourseById(courseId);
        if (!course.getInstructor().getId().equals(instructorId)) {
            return new ResponseEntity<>("Unauthorized: You do not own this course", HttpStatus.FORBIDDEN);
        }

        try {
            LocalDateTime parsedDeadline = LocalDateTime.parse(deadline);

            Assignment assignment = new Assignment();
            assignment.setTitle(title);
            assignment.setDescription(description);
            assignment.setDeadline(parsedDeadline);

            Assignment created = assignmentService.createAssignment(assignment, courseId, file);

            AssignmentCreatedEvent event = new AssignmentCreatedEvent(created.getId());
            eventBus.publish(event);

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

        if (!Constants.ROLE_STUDENT.equals(role)) {
            return new ResponseEntity<>(Constants.UNAUTHORIZED, HttpStatus.FORBIDDEN);
        }
        Assignment assignment = assignmentService.getAssignmentById(assignmentId);
        boolean enrolled = courseService.isEnrolled(assignment.getCourse().getId(), studentId);
        if(!enrolled){
            return ResponseEntity.status(403).body("You must be enrolled in the course to be able to view its content");
        }

        try {
            submissionService.submitSolution(assignmentId, studentId, file);
            return new ResponseEntity<>("Solution submitted successfully!", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error submitting solution: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Assignment>> getAllAssignments(@RequestHeader("Authorization") String token, @PathVariable Long courseId) {
        String role = jwtConfig.getRoleFromToken(token);
        Long studentId = jwtConfig.getUserIdFromToken(token);

        if (!Constants.ROLE_STUDENT.equals(role)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        boolean enrolled = courseService.isEnrolled(courseId, studentId);
        if(!enrolled){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Assignment> assignments = assignmentService.getAllAssignments(courseId);
        return ResponseEntity.ok(assignments);
    }


    @GetMapping("/submission/{assignmentId}")
    public ResponseEntity<List<Submission>> getAllSubmissions(@RequestHeader("Authorization") String token, @PathVariable Long assignmentId) {
        String role = jwtConfig.getRoleFromToken(token);
        Long instructorId = jwtConfig.getUserIdFromToken(token);

        if (!Constants.ROLE_INSTRUCTOR.equals(role)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Assignment assignment = assignmentService.getAssignmentById(assignmentId);
        if(!assignment.getCourse().getInstructor().getId().equals(instructorId)){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        List<Submission> submissions = submissionService.getAllSubmissions(assignmentId);
        return ResponseEntity.ok(submissions);
    }


    @GetMapping("/feedback/create")
    public ResponseEntity<String> createFeedback(@RequestHeader("Authorization") String token, @RequestBody FeedbackRequest feedbackRequest) {
        String role = jwtConfig.getRoleFromToken(token);
        Long instructorId = jwtConfig.getUserIdFromToken(token);

        if (!Constants.ROLE_INSTRUCTOR.equals(role)) {
            return new ResponseEntity<>(Constants.UNAUTHORIZED, HttpStatus.FORBIDDEN);
        }
        Course course = submissionService.getSubmission(feedbackRequest.getSubmissionID()).getAssignment().getCourse();
        if(!course.getInstructor().getId().equals(instructorId)){
            return new ResponseEntity<>("Unauthorized: You do not own this course", HttpStatus.FORBIDDEN);
        }

        try
        {
            Feedback feedback = feedbackService.giveFeedback(feedbackRequest);
            FeedbackCreatedEvent event = new FeedbackCreatedEvent(feedback.getId());
            eventBus.publish(event);
            return new ResponseEntity<>("Feedback given successfully", HttpStatus.CREATED);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/feedback/{submissionId}")
    public ResponseEntity<FeedbackRequest> getFeedback(@RequestHeader("Authorization") String token, @PathVariable Long submissionId) {

        String role = jwtConfig.getRoleFromToken(token);
        Long studentId = jwtConfig.getUserIdFromToken(token);

        if (!Constants.ROLE_STUDENT.equals(role)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if(!submissionService.getSubmission(submissionId).getStudent().getId().equals(studentId)){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            FeedbackRequest feedback = feedbackService.getFeedbackBySubmission(submissionId);
            if (feedback == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(feedback);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
