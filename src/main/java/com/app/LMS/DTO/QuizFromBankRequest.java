package com.app.LMS.DTO;

import com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.model.Question;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.List;

public class QuizFromBankRequest {
    @NotBlank
    private String title;
    @NotBlank
    private Date startDate;
    @NotBlank
    private String duration; // duration in minutes
    @NotBlank
    @NotNull
    private List<Long> questions;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<Long> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Long> questions) {
        this.questions = questions;
    }
}


