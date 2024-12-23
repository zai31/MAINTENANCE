package com.app.LMS.attendanceManagement.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.app.LMS.attendanceManagement.model.Attendance;
import com.app.LMS.attendanceManagement.model.OTP;
import com.app.LMS.attendanceManagement.repository.AttendanceRepository;
import com.app.LMS.userManagement.model.User;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public Boolean markAttendance(User student, OTP otp) {
        if (otp == null || LocalDateTime.now().isAfter(otp.getExpirationTime())) {
            return Boolean.FALSE;
        }

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setOtp(otp);
        attendance.setTimestamp(LocalDateTime.now());

        attendanceRepository.save(attendance);
        return Boolean.TRUE;
    }

    // Fetch attendance records for a student and course
    public List<Attendance> getAttendanceByStudentAndCourse(User student, Long courseId) {
        return attendanceRepository.findByStudentAndOtp_Course_Id(student, courseId);
    }

    public boolean isMarked(String OtpCode, Long studentId){
        return attendanceRepository.existsByOtp_CodeAndStudent_Id(OtpCode, studentId);
    }

    // Fetch attendance records for a course
    public List<Attendance> getAttendanceByCourse(Long courseId) {
        return attendanceRepository.findByOtp_Course_Id(courseId);
    }

}
