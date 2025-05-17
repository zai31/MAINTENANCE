package com.app.LMS.attendanceManagement.model;

import java.time.LocalDateTime;
import java.util.List;

public class AttendanceResponse {
    private Long studentId;
    private Long courseId;
    private int classesAttended;
    private List<LocalDateTime> attendedClassDates;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public int getClassesAttended() {
        return classesAttended;
    }

    public void setClassesAttended(int classesAttended) {
        this.classesAttended = classesAttended;
    }

    public List<LocalDateTime> getAttendedClassDates() {
        return attendedClassDates;
    }

    public void setAttendedClassDates(List<LocalDateTime> attendedClassDates) {
        this.attendedClassDates = attendedClassDates;
    }

    public AttendanceResponse() {}

    public AttendanceResponse(Long studentId, Long courseId, int classesAttended, List<LocalDateTime> attendedClassDates) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.classesAttended = classesAttended;
        this.attendedClassDates = attendedClassDates;
    }


}

