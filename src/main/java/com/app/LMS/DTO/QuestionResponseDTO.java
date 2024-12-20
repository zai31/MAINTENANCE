package com.app.LMS.DTO;

import java.util.List;

public class QuestionResponseDTO {
    private Long id;
    private String question;
    private int points;
    private List<String> options;

    public QuestionResponseDTO(Long id, String question, int points, List<String> options) {
        this.id = id;
        this.question = question;
        this.points = points;
        this.options = options;
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getQuestion() {
        return this.question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public int getPoints() {
        return this.points;
    }
    public void setPoints(int points) {
        this.points = points;
    }
    public List<String> getOptions() {
        return this.options;
    }
    public void setOptions(List<String> options) {
        this.options = options;
    }
}
