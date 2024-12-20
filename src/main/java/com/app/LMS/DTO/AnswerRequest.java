package com.app.LMS.DTO;

public class AnswerRequest {
    private Long questionId;
    private String answer;
    public Long getQuestionId() {
        return this.questionId;
    }
    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
    public String getAnswer() {
        return this.answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
