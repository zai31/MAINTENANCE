package com.app.LMS.attendanceManagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.app.LMS.courseManagement.model.Course;

@Entity
public class OTP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // OTP code cannot be null
    private String code; // The OTP code

    private LocalDateTime classDateTime = LocalDateTime.now(); // Class start date and time

    private LocalDateTime expirationTime; // Expiration time (class start + 30 minutes)

    @ManyToOne
    @JoinColumn(nullable = false) // Association with a Course
    private Course course;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getClassDateTime() {
        return this.classDateTime;
    }

    public void setClassDateTime(LocalDateTime classDateTime) {
        this.classDateTime = classDateTime;
    }

    public LocalDateTime getExpirationTime() {
        return this.expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
