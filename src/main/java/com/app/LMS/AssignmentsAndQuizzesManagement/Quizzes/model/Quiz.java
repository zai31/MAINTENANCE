package com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.model;

import com.app.LMS.courseManagement.model.Course;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.List;

@Entity
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String title;
    @NotNull
    @Future
    private Date startDate;
    @NotNull
    private String duration; // duration in minutes
    @NotNull
    @ManyToOne
    private Course course;

//    @OneToMany(mappedBy = "id", cascade = CascadeType.ALL, orphanRemoval = true)

    @OneToMany
    private  List<Question> questions;


    public Long getId() {
        return id;
    }

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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<Question> getQuestions() {
        return questions;
    }
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }


}
