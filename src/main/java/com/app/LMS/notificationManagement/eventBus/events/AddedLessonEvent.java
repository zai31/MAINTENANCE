package com.app.LMS.notificationManagement.eventBus.events;

import com.app.LMS.notificationManagement.eventBus.Event;

public class AddedLessonEvent extends Event {
    private final Long courseId;

    public AddedLessonEvent(Long courseId) {
        this.courseId = courseId;
    }

    public Long getCourseId() {
        return this.courseId;
    }

}
