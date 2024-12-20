package com.app.LMS.DTO;

import java.util.Date;
import java.util.List;

public class QuizDetailsDTO {
    private Long id;
    private String title;
    private Date startDate;
    private int durationInMinutes;
    private List<QuestionResponseDTO> questions;

    public QuizDetailsDTO(Long id, String title, Date startDate, int durationInMinutes, List<QuestionResponseDTO> questions) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.durationInMinutes = durationInMinutes;
        this.questions = questions;
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Date getStartDate() {
        return this.startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public int getDurationInMinutes() {
        return this.durationInMinutes;
    }
    public void setDurationInMinutes(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public List<QuestionResponseDTO> getQuestions() {
        return this.questions;
    }

    public void setQuestions(List<QuestionResponseDTO> questions) {
        this.questions = questions;
    }
}
