package com.app.LMS.notificationManagement.eventBus.events;

import com.app.LMS.notificationManagement.eventBus.Event;

public class AssignmentCreatedEvent extends Event {
    private final Long assignmentId;

    public AssignmentCreatedEvent(Long assignmentId){
        this.assignmentId = assignmentId;
    }

    public Long getAssignmentId() {
        return this.assignmentId;
    }
}
