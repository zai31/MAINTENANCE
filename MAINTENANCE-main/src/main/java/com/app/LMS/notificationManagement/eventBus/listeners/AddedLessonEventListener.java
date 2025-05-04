package com.app.LMS.notificationManagement.eventBus.listeners;

import com.app.LMS.DTO.StudentInfoDTO;
import com.app.LMS.notificationManagement.eventBus.EventListener;
import com.app.LMS.notificationManagement.eventBus.events.AddedLessonEvent;
import com.app.LMS.notificationManagement.notification.NotificationService;
import com.app.LMS.userManagement.service.UserService;
import com.app.LMS.userManagement.model.User;
import com.app.LMS.courseManagement.model.Enrollment;
import com.app.LMS.courseManagement.service.CourseService;
import com.app.LMS.courseManagement.service.LessonService;

import java.util.List;

public class AddedLessonEventListener implements EventListener<AddedLessonEvent> {
    private final NotificationService notificationService;
    private final CourseService courseService;

    public AddedLessonEventListener(NotificationService notificationService, CourseService courseService) {
        this.notificationService = notificationService;
        this.courseService = courseService;
    }

    @Override
    public void handle(AddedLessonEvent event){
        Long courseId = event.getCourseId();
        List<StudentInfoDTO> enrolledStudents = courseService.getEnrolledStudents(courseId);

        String message = "New lesson was added in course " + courseId;

        // Send in app notifications
        for(StudentInfoDTO student : enrolledStudents) {
            notificationService.createNotification(student.getStudentId(), message);
        }
        // Send mail notifications
        for(StudentInfoDTO student : enrolledStudents){
            notificationService.sendEmailNotification(student.getEmail(), "New Lesson Added", message);
        }
    }
}
