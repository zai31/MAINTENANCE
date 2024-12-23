package com.app.LMS.DTO;

public class QuizAttemptDTO {
    private Long quizAttemptId;
    private Long quizId;
    private int score;

    public QuizAttemptDTO(Long quizAttemptId, Long quizId, int score) {
        this.quizAttemptId = quizAttemptId;
        this.quizId = quizId;
        this.score = score;
    }
    public Long getQuizAttemptId() {
        return this.quizAttemptId;
    }
    public void setQuizAttemptId(Long quizAttemptId) {
        this.quizAttemptId = quizAttemptId;
    }
    public Long getQuizId() {
        return this.quizId;
    }
    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }
    public int getScore() {
        return this.score;
    }
    public void setScore(int score) {
        this.score = score;
    }
}
