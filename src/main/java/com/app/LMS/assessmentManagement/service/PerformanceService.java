package com.app.LMS.assessmentManagement.service;

import com.app.LMS.DTO.FeedbackDTO;
import com.app.LMS.DTO.QuizAttemptDTO;
import com.app.LMS.assessmentManagement.model.Feedback;
import com.app.LMS.assessmentManagement.model.QuizAttempt;
import com.app.LMS.assessmentManagement.repository.FeedbackRepository;
import com.app.LMS.assessmentManagement.repository.QuizAttemptRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PerformanceService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final FeedbackRepository feedbackRepository;

    public PerformanceService(QuizAttemptRepository quizAttemptRepository, FeedbackRepository feedbackRepository) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.feedbackRepository = feedbackRepository;
    }

    public List<QuizAttemptDTO> getQuizAttemptsByStudentId(Long studentId, Long courseId) {
        List<QuizAttempt> quizAttempts = quizAttemptRepository.findByStudentIdAndQuizCourseId(studentId, courseId);

        // Convert QuizAttempt entities to QuizAttemptDTOs
        return quizAttempts.stream()
                .map(quizAttempt -> new QuizAttemptDTO(
                        quizAttempt.getId(),
                        quizAttempt.getQuiz().getId(),
                        quizAttempt.getScore()
                ))
                .collect(Collectors.toList());
    }

    public List<FeedbackDTO> getFeedbacksByStudentId(Long studentId, Long courseId) {
        List<Feedback> feedbacks = feedbackRepository.findBySubmissionStudentIdAndSubmissionAssignmentCourseId(studentId, courseId);

        // Convert Feedback entities to FeedbackDTOs
        return feedbacks.stream()
                .map(feedback -> new FeedbackDTO(
                        feedback.getId(),
                        feedback.getComments(),
                        feedback.getGrade()
                ))
                .collect(Collectors.toList());
    }
}
