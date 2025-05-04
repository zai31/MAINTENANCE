package com.app.LMS.assessmentManagement.model;

import com.app.LMS.userManagement.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.List;

@Entity
public class QuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    private Quiz quiz;  // The quiz being attempted

    @ManyToOne
    @NotNull
    private User student;  // The student attempting the quiz

    @NotNull
    private Date attemptDate;  // The date and time the attempt was made

    private int score;  // The score the student achieved for this attempt

    @OneToMany(mappedBy = "quizAttempt")
    private List<Answer> questionAnswers;  // List of answers the student submitted for each question in the quiz

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public User getStudent() {
        return this.student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Date getAttemptDate() {
        return attemptDate;
    }

    public void setAttemptDate(Date attemptDate) {
        this.attemptDate = attemptDate;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<Answer> getQuestionAnswers() {
        return this.questionAnswers;
    }

    public void setQuestionAnswers(List<Answer> questionAnswers) {
        this.questionAnswers = questionAnswers;
    }
}
