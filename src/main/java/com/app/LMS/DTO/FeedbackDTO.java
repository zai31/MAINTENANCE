package com.app.LMS.DTO;

public class FeedbackDTO {
    private Long feedbackId;
    private String feedbackComment;
    private Double grade;

    public FeedbackDTO(Long feedbackId, String feedbackComment, Double grade) {
        this.feedbackId = feedbackId;
        this.feedbackComment = feedbackComment;
        this.grade = grade;
    }

    public Long getFeedbackId() {
        return this.feedbackId;
    }
    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
    }
    public String getFeedbackComment() {
        return this.feedbackComment;
    }
    public void setFeedbackComment(String feedbackComment) {
        this.feedbackComment = feedbackComment;
    }
    public Double getGrade() {
        return this.grade;
    }
    public void setGrade(Double grade) {
        this.grade = grade;
    }
}
