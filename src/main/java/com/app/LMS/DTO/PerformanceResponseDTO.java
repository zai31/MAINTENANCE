package com.app.LMS.DTO;

import java.util.List;

public class PerformanceResponseDTO {
    private List<QuizAttemptDTO> quizAttempts;
    private List<FeedbackDTO> feedbacks;

    // Default constructor
    public PerformanceResponseDTO() {
    }

    // Parameterized constructor
    public PerformanceResponseDTO(List<QuizAttemptDTO> quizAttempts, List<FeedbackDTO> feedbacks) {
        this.quizAttempts = quizAttempts;
        this.feedbacks = feedbacks;
    }

    // Getters and Setters
    public List<QuizAttemptDTO> getQuizAttempts() {
        return quizAttempts;
    }

    public void setQuizAttempts(List<QuizAttemptDTO> quizAttempts) {
        this.quizAttempts = quizAttempts;
    }

    public List<FeedbackDTO> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<FeedbackDTO> feedbacks) {
        this.feedbacks = feedbacks;
    }



}
