package com.app.LMS.notificationManagement.eventBus.events;

import com.app.LMS.notificationManagement.eventBus.Event;

public class MaterialUploadedEvent extends Event {
    private final Long lessonId;

    public MaterialUploadedEvent(Long lessonId) {
        this.lessonId = lessonId;
    }

    public Long getLessonId() {
        return this.lessonId;
    }

}
