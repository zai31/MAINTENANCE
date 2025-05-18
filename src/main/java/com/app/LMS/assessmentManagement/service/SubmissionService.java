package com.app.LMS.assessmentManagement.service;

import com.app.LMS.assessmentManagement.model.Assignment;
import com.app.LMS.assessmentManagement.model.Submission;
import com.app.LMS.assessmentManagement.repository.AssignmentRepository;
import com.app.LMS.assessmentManagement.repository.SubmissionRepository;
import com.app.LMS.userManagement.model.User;
import com.app.LMS.userManagement.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    public SubmissionService(SubmissionRepository submissionRepository, AssignmentRepository assignmentRepository, UserRepository userRepository) {
        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
    }

    public Submission submitSolution(Long assignmentId, Long studentId, MultipartFile file) throws IOException {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid assignment ID"));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student ID"));

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setSubmittedAt(LocalDateTime.now());


        submission = submissionRepository.save(submission);


        Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads", "courses",
                String.valueOf(assignment.getCourse().getId()), "assignments",
                String.valueOf(assignment.getId()), "submissions",
                String.valueOf(submission.getId()));

        File directory = uploadDir.toFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }


        Path filePath = uploadDir.resolve(file.getOriginalFilename());
        file.transferTo(filePath.toFile());


        submission.setFilePath(filePath.toString());
        return submissionRepository.save(submission);
    }


    public List<Submission> getAllSubmissions(Long assignmentId){
        return submissionRepository.findByAssignmentId(assignmentId);
    }

    public Submission getSubmission(Long submissionId){
        return submissionRepository.findById(submissionId).orElse(null);
    }
}
