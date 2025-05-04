package com.app.LMS.assessmentManagement.service;

import com.app.LMS.assessmentManagement.model.Assignment;
import com.app.LMS.assessmentManagement.model.Submission;
import com.app.LMS.assessmentManagement.repository.AssignmentRepository;
import com.app.LMS.assessmentManagement.repository.SubmissionRepository;
import com.app.LMS.userManagement.model.User;
import com.app.LMS.userManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

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

        // Save the submission to generate the submission ID
        submission = submissionRepository.save(submission);

        // Define the upload directory
        String uploadDir = System.getProperty("user.dir") + "/uploads/courses/" + assignment.getCourse().getId() + "/assignments/" + assignment.getId() + "/submissions/" + submission.getId();
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Save the file
        String filePath = uploadDir + "/" + file.getOriginalFilename();
        File destinationFile = new File(filePath);
        file.transferTo(destinationFile);

        // Update the submission with the file path
        submission.setFilePath(filePath);
        return submissionRepository.save(submission);
    }

    public List<Submission> getAllSubmissions(Long assignmentId){
        return submissionRepository.findByAssignmentId(assignmentId);
    }

    public Submission getSubmission(Long submissionId){
        return submissionRepository.findById(submissionId).orElse(null);
    }
}
