package com.app.LMS.notificationManagement;

import com.app.LMS.assessmentManagement.service.AssignmentService;
import com.app.LMS.assessmentManagement.service.FeedbackService;
import com.app.LMS.assessmentManagement.service.QuizService;
import com.app.LMS.courseManagement.service.CourseService;
import com.app.LMS.courseManagement.service.LessonService;
import com.app.LMS.notificationManagement.eventBus.events.*;
import com.app.LMS.notificationManagement.eventBus.listeners.*;
import com.app.LMS.userManagement.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import com.app.LMS.notificationManagement.notification.NotificationService;
import com.app.LMS.notificationManagement.eventBus.EventBus;
import com.app.LMS.notificationManagement.eventBus.GeneralEventBus;

@Configuration
public class NotificationManagementConfiguration {

    @Bean
    public EventBus eventBus(NotificationService notificationService, CourseService courseService, UserService userService, LessonService lessonService, QuizService quizService, AssignmentService assignmentService, FeedbackService feedbackService) {
        GeneralEventBus eventBus = new GeneralEventBus();
        eventBus.register(EnrollmentEvent.class, new EnrollmentEventListener(notificationService, courseService, userService));
        eventBus.register(AddedLessonEvent.class, new AddedLessonEventListener(notificationService, courseService));
        eventBus.register(MaterialUploadedEvent.class, new MaterialUploadedEventListener(notificationService, courseService, lessonService));
        eventBus.register(QuizCreatedEvent.class, new QuizCreatedEventListener(quizService, courseService, notificationService));
        eventBus.register(AssignmentCreatedEvent.class, new AssignmentCreatedEventListener(assignmentService, courseService, notificationService));
        eventBus.register(FeedbackCreatedEvent.class, new FeedbackCreatedEventListener(feedbackService, notificationService));
        return eventBus;
    }
}
