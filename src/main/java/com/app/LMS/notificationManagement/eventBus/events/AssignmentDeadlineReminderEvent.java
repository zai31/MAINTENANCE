package com.app.LMS.notificationManagement.eventBus.events;

import com.app.LMS.notificationManagement.eventBus.Event;

public class AssignmentDeadlineReminderEvent extends Event {
    private final Long assignmentId;

    public AssignmentDeadlineReminderEvent(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

}