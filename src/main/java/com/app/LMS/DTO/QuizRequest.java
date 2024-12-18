package com.app.LMS.DTO;

import com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.model.Question;


import java.util.Date;
import java.util.List;


public class QuizRequest {
    @NotBlank
    private String title;
    @NotBlank
    private Date startDate;
    @NotBlank
    private String duration; // duration in minutes
    @NotBlank
    private List<Question> questions;

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

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
