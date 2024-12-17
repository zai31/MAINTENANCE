package com.app.LMS.attendanceManagement.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.app.LMS.userManagement.model.User;
import com.app.LMS.attendanceManagement.model.OTP;

@Entity
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User student; // Student attending the class

    @ManyToOne
    private OTP otp; // OTP entered by the student

    private LocalDateTime timestamp; // Time the attendance was marked

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getStudent() {
        return this.student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public OTP getOtp() {
        return this.otp;
    }

    public void setOtp(OTP otp) {
        this.otp = otp;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
