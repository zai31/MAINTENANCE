package com.app.LMS.DTO;

import java.util.List;

public class SubmitQuizRequest {
    private Long quizId;
    private List<AnswerRequest> answers;
    private Long studentId;

    public Long getQuizId() {
        return this.quizId;
    }
    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }
    public List<AnswerRequest> getAnswers() {
        return this.answers;
    }
    public void setAnswers(List<AnswerRequest> answers) {
        this.answers = answers;
    }
    public Long getStudentId() {
        return this.studentId;
    }
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
}
