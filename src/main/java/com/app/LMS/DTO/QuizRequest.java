package com.app.LMS.DTO;

import java.util.Date;

public class QuizRequest {
    private int quizId;
    private String title;
    private Date startDate;
    private int duration;
    private Long courseID;
    private int numberOfQuestions;

    //setters and getters
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

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
    public Long getCourseID() {
        return this.courseID;
    }
    public void setId(Long id) {
        this.courseID = id;
    }

    public int getNumberOfQuestions() {
        return this.numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public int getQuizId() {
        return this.quizId;
    }
}