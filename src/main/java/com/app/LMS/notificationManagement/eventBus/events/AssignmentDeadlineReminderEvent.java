package com.app.LMS.notificationManagement.eventBus.events;

import com.app.LMS.notificationManagement.eventBus.Event;
import com.app.LMS.notificationManagement.eventBus.EventBus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

public class AssignmentDeadlineReminderEvent extends Event {
    private final Long assignmentId;

    public AssignmentDeadlineReminderEvent(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }
    @RestController
    @RequestMapping("/api/notifications")
    public class NotificationTestController {

        private final EventBus eventBus;

        public NotificationTestController(EventBus eventBus) {
            this.eventBus = eventBus;
        }

        @PostMapping("/test-deadline-reminder")
        public String triggerReminder(@RequestParam Long assignmentId) {
            eventBus.publish(new AssignmentDeadlineReminderEvent(assignmentId));
            return "Reminder triggered for assignment ID " + assignmentId;
        }

    }
}