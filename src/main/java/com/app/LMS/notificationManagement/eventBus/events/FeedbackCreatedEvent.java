package com.app.LMS.notificationManagement.eventBus.events;

import com.app.LMS.notificationManagement.eventBus.Event;

public class FeedbackCreatedEvent extends Event {
    private final Long feedbackId;

    public FeedbackCreatedEvent(Long feedbackId) {
        this.feedbackId = feedbackId;
    }
    public Long getFeedbackId() {
        return this.feedbackId;
    }
}
