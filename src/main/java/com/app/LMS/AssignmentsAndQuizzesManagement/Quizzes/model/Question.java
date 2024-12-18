package com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.model;

import com.app.LMS.DTO.NotBlank;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.util.List;

//@MappedSuperclass
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    @Column(unique = true)
    private String question;
    @NotNull
    @Enumerated(EnumType.STRING)
    private QuestionType type;
    public enum QuestionType {
        MCQ,
        TRUE_FALSE,
        SHORT_ANSWER
    }
    @ElementCollection
    private List<String> options;
    @NotNull
    private float points;
    @NotNull
    @NotEmpty
    private  String answer;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }


    public void setType(QuestionType type) {
        this.type = type;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public QuestionType getType() {
        return type;
    }




    public float getPoints() {
        return points;
    }

    public void setPoints(float points) {
        this.points = points;
    }

    public Long getId() {
        return id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
