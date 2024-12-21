package com.app.LMS.notificationManagement.eventBus.listeners;

import com.app.LMS.courseManagement.model.Course;
import com.app.LMS.courseManagement.service.CourseService;
import com.app.LMS.notificationManagement.eventBus.EventListener;
import com.app.LMS.notificationManagement.eventBus.events.EnrollmentEvent;
import com.app.LMS.notificationManagement.notification.NotificationService;
import com.app.LMS.userManagement.model.User;
import com.app.LMS.userManagement.service.UserService;

public class EnrollmentEventListener implements EventListener<EnrollmentEvent> {

    private final NotificationService notificationService;
    private final CourseService courseService;
    private final UserService userService;

    public EnrollmentEventListener(NotificationService notificationService, CourseService courseService, UserService userService) {
        this.notificationService = notificationService;
        this.courseService = courseService;
        this.userService = userService;
    }

    @Override
    public void handle(EnrollmentEvent event) {
        Long studentId = event.getStudentId();
        Long courseId = event.getCourseId();
        User student = userService.findById(studentId).orElse(null);
        Course course = courseService.findCourseById(courseId);
        User instructor = course.getInstructor();

        String studentMessage = "You have successfully enrolled in course: " + course.getTitle();
        String instructorMessage = "A new student has enrolled in your course: " + course.getTitle() + "\n Student ID: " + studentId;
        // Send in app notifications
        notificationService.createNotification(studentId, studentMessage);
        notificationService.createNotification(course.getInstructor().getId(), instructorMessage);
        // send mail notifications
        notificationService.sendEmailNotification(student.getEmail(),"Successfull Enrollment", studentMessage);
        notificationService.sendEmailNotification(instructor.getEmail(),"Student Enrolled In Your Course", instructorMessage);
    }
}
