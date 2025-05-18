package com.app.LMS.notificationManagement.eventBus.listeners;

import com.app.LMS.assessmentManagement.model.Assignment;
import com.app.LMS.assessmentManagement.service.AssignmentService;
import com.app.LMS.notificationManagement.eventBus.EventBus;
import com.app.LMS.notificationManagement.eventBus.EventListener;
import com.app.LMS.notificationManagement.eventBus.events.AssignmentDeadlineReminderEvent;
import com.app.LMS.notificationManagement.notification.NotificationService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class AssignmentDeadlineReminderListener implements EventListener<AssignmentDeadlineReminderEvent> {

    private final NotificationService notificationService;
    private final AssignmentService assignmentService;
    private final EventBus eventBus;

    public AssignmentDeadlineReminderListener(NotificationService notificationService,
                                              AssignmentService assignmentService,
                                              EventBus eventBus) {
        this.notificationService = notificationService;
        this.assignmentService = assignmentService;
        this.eventBus = eventBus;
    }

    @PostConstruct
    public void register() {
        eventBus.register(AssignmentDeadlineReminderEvent.class, this);
    }

    @Override
    public void handle(AssignmentDeadlineReminderEvent event) {
        Assignment assignment = assignmentService.getAssignmentById(event.getAssignmentId());
        assignment.getCourse().getUsers().stream()
                .filter(user -> user.getRole() == com.app.LMS.userManagement.model.User.Role.STUDENT)
                .forEach(student -> {
                    String message = "Reminder: Assignment \"" + assignment.getTitle() + "\" is due in 24 hours!";
                    notificationService.createNotification(student.getId(), message);

                });
    }
}
