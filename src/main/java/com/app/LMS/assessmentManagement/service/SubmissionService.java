package com.app.LMS.assessmentManagement.service;

import com.app.LMS.assessmentManagement.model.Submission;
import com.app.LMS.assessmentManagement.repository.SubmissionRepository;
import org.springframework.stereotype.Service;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;

    public SubmissionService(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    public Submission createSubmission(Submission submission) {
        return submissionRepository.save(submission);
    }
}
