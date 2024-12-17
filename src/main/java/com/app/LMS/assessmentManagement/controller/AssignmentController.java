package com.app.LMS.assessmentManagement.controller;

import com.app.LMS.assessmentManagement.model.Assignment;
import com.app.LMS.assessmentManagement.service.AssignmentService;
import com.app.LMS.config.JwtConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final JwtConfig jwtConfig;

    public AssignmentController(AssignmentService assignmentService, JwtConfig jwtConfig) {
        this.assignmentService = assignmentService;
        this.jwtConfig = jwtConfig;
    }

    @PostMapping(
            value = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> createAssignment(
            @RequestHeader("Authorization") String token,
            @RequestBody Assignment assignment
    ) {
        try {
            Long instructorId = jwtConfig.getUserIdFromToken(token);
            assignmentService.createAssignment(assignment, instructorId);
            return new ResponseEntity<>("Assignment created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating assignment: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
