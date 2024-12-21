package com.app.LMS.notificationManagement.eventBus.listeners;

import com.app.LMS.DTO.StudentInfoDTO;
import com.app.LMS.assessmentManagement.model.Quiz;
import com.app.LMS.assessmentManagement.service.QuizService;
import com.app.LMS.courseManagement.model.Course;
import com.app.LMS.courseManagement.service.CourseService;
import com.app.LMS.notificationManagement.eventBus.EventListener;
import com.app.LMS.notificationManagement.eventBus.events.QuizCreatedEvent;
import com.app.LMS.notificationManagement.notification.NotificationService;
import java.util.List;

public class QuizCreatedEventListener implements EventListener<QuizCreatedEvent>{
    private final QuizService quizService;
    private final CourseService courseService;
    private final NotificationService notificationService;
    public QuizCreatedEventListener(QuizService quizService, CourseService courseService, NotificationService notificationService) {
        this.quizService = quizService;
        this.courseService = courseService;
        this.notificationService = notificationService;
    }

    @Override
    public void handle(QuizCreatedEvent event) {
        Long quizId = event.getQuizId();
        Quiz quiz = quizService.getById(quizId);

        List<StudentInfoDTO> enrolledStudents = courseService.getEnrolledStudents(quiz.getCourse().getId());

        String message = "A new Quiz for " + quiz.getCourse().getTitle() + " course is scheduled at " + quiz.getStartDate();
        // Send in app notifications
        for(StudentInfoDTO student : enrolledStudents) {
            notificationService.createNotification(student.getStudentId(), message);
        }
        // Send mail notifications
        for(StudentInfoDTO student : enrolledStudents){
            notificationService.sendEmailNotification(student.getEmail(), "New Quiz Scheduled", message);
        }
    }
}