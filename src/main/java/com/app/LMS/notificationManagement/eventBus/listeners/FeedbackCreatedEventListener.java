package com.app.LMS.notificationManagement.eventBus.listeners;

import com.app.LMS.DTO.StudentInfoDTO;
import com.app.LMS.assessmentManagement.model.Assignment;
import com.app.LMS.assessmentManagement.model.Feedback;
import com.app.LMS.assessmentManagement.service.FeedbackService;
import com.app.LMS.notificationManagement.eventBus.EventListener;
import com.app.LMS.notificationManagement.eventBus.events.FeedbackCreatedEvent;
import com.app.LMS.notificationManagement.notification.NotificationService;
import com.app.LMS.userManagement.model.User;

public class FeedbackCreatedEventListener implements EventListener<FeedbackCreatedEvent> {
    private final FeedbackService feedbackService;
    private final NotificationService notificationService;

    public FeedbackCreatedEventListener(FeedbackService feedbackService, NotificationService notificationService) {
        this.feedbackService = feedbackService;
        this.notificationService = notificationService;
    }

    @Override
    public void handle(FeedbackCreatedEvent event) {
        Long feedbackId = event.getFeedbackId();
        Feedback feedback = feedbackService.getFeedbackById(feedbackId);
        User student = feedback.getSubmission().getStudent();
        Assignment assignment = feedback.getSubmission().getAssignment();

        String message = "Your assignment in " + assignment.getCourse().getTitle() + " was graded";

        // Send in app notifications
        notificationService.createNotification(student.getId(), message);
        // Send mail notifications
        notificationService.sendEmailNotification(student.getEmail(), "Assignment Graded", message);
    }
}
