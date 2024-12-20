package com.app.LMS.assessmentManagement.model;

import com.app.LMS.courseManagement.model.Course;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class QuestionBank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Course course;  // The course associated with this question bank

    @OneToMany
    private List<Question> questions;  // List of questions in the question bank

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
