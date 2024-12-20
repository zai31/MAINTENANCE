package com.app.LMS.assessmentManagement.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The question this answer relates to
    @ManyToOne
    @NotNull
    private Question question;

    // The quiz attempt this answer belongs to
    @ManyToOne
    @NotNull
    private QuizAttempt quizAttempt;

    // The student's response to the question (could be text or choice, depending on question type)
    @NotNull
    private String response;

    // Whether the student's response is correct or not
    private boolean isCorrect;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public QuizAttempt getQuizAttempt() {
        return quizAttempt;
    }

    public void setQuizAttempt(QuizAttempt quizAttempt) {
        this.quizAttempt = quizAttempt;
    }

    public String getResponse() {
        return this.response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}
