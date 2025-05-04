package com.app.LMS.assessmentManagement.service;

import com.app.LMS.assessmentManagement.model.Feedback;
import com.app.LMS.assessmentManagement.model.Submission;
import com.app.LMS.assessmentManagement.repository.FeedbackRepository;
import com.app.LMS.assessmentManagement.repository.SubmissionRepository;
import org.springframework.stereotype.Service;
import com.app.LMS.DTO.FeedbackRequest;

import java.util.List;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final SubmissionRepository submissionRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, SubmissionRepository submissionRepository) {
        this.feedbackRepository = feedbackRepository;
        this.submissionRepository = submissionRepository;
    }

    public Feedback giveFeedback(FeedbackRequest request) {
        // Check if submission exists
        Submission submission = submissionRepository.findById(request.getSubmissionID()).orElseThrow(() -> new IllegalArgumentException("Submission not found"));

        // Check if feedback already exists
        if (feedbackRepository.findBySubmissionId(request.getSubmissionID()) != null) {
            throw new IllegalStateException("Feedback already provided for this submission");
        }

        // Create feedback
        Feedback feedback = new Feedback();
        feedback.setSubmission(submission);
        feedback.setComments(request.getComment());
        feedback.setGrade(request.getGrade());

        return feedbackRepository.save(feedback);
    }

    public Feedback getFeedbackById(Long id) {
        return feedbackRepository.findById(id).orElseThrow(() -> new RuntimeException("Feedback with ID " + id + " not found"));
    }

    public FeedbackRequest getFeedbackBySubmission(Long submissionId) {
        Feedback feedback = feedbackRepository.findBySubmissionId(submissionId);
        if (feedback == null) {
            throw new IllegalArgumentException("Submission with ID " + submissionId + " not found");
        }
        FeedbackRequest request = new FeedbackRequest();
        request.setSubmissionID(feedback.getId());
        request.setComment(feedback.getComments());
        request.setGrade(feedback.getGrade());
        return request;
    }

}
