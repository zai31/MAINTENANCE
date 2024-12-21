package com.app.LMS.notificationManagement.eventBus.events;

import com.app.LMS.notificationManagement.eventBus.Event;
public class EnrollmentEvent extends Event {

    private final Long studentId;
    private final Long courseId;

    public EnrollmentEvent(Long studentId, Long courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public Long getCourseId() {
        return courseId;
    }
}
