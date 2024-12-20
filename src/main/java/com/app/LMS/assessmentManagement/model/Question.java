package com.app.LMS.assessmentManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String questionText; // The actual question

    @NotNull
    @Enumerated(EnumType.STRING)  // Enums are stored as strings in the database
    private QuestionType type;  // Type of question (MCQ, True/False, Short Answer)
    public enum QuestionType {
        MCQ,      // Multiple Choice Question
        TRUE_FALSE, // True/False Question
        SHORT_ANSWER // Short Answer Question
    }

    @NotNull
    private int points; // Points assigned to the question

    private String correctAnswer; // Correct answer (can be generic for all types)

    @ElementCollection
    private List<String> options;  // List of options for MCQs or similar questions

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionText() {
        return this.questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}
