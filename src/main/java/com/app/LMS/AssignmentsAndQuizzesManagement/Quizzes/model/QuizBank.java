package com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.model;

import com.app.LMS.courseManagement.model.Course;
import jakarta.persistence.*;

import java.util.List;
@Entity
public class QuizBank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Course course;
    @OneToMany
    private List<Question> questions;

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
