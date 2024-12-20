package com.app.LMS.DTO;

public class FeedbackRequest {
    private Long submissionID;
    private String comment;
    private Double grade;

    public Long getSubmissionID() {
        return this.submissionID;
    }

    public void setSubmissionID(Long submissionID) {
        this.submissionID = submissionID;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    public Double getGrade() {
        return this.grade;
    }
    public void setGrade(Double grade) {
        this.grade = grade;
    }
}
