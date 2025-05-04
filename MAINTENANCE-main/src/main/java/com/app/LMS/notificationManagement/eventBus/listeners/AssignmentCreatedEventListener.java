package com.app.LMS.notificationManagement.eventBus.listeners;

import com.app.LMS.DTO.StudentInfoDTO;
import com.app.LMS.assessmentManagement.model.Assignment;
import com.app.LMS.assessmentManagement.service.AssignmentService;
import com.app.LMS.courseManagement.service.CourseService;
import com.app.LMS.notificationManagement.eventBus.EventListener;
import com.app.LMS.notificationManagement.eventBus.events.AssignmentCreatedEvent;
import com.app.LMS.notificationManagement.notification.NotificationService;
import java.util.List;

public class AssignmentCreatedEventListener implements EventListener<AssignmentCreatedEvent> {
    private final AssignmentService assignmentService;
    private final CourseService courseService;
    private final NotificationService notificationService;

    public AssignmentCreatedEventListener(AssignmentService assignmentService, CourseService courseService, NotificationService notificationService) {
        this.assignmentService = assignmentService;
        this.courseService = courseService;
        this.notificationService = notificationService;
    }

    @Override
    public void handle(AssignmentCreatedEvent event) {
        Long assignmentId = event.getAssignmentId();
        Assignment assignment = assignmentService.getAssignmentById(assignmentId);
        List<StudentInfoDTO> enrolledStudents = courseService.getEnrolledStudents(assignment.getCourse().getId());

        String message = "A new assignment is uploaded for " + assignment.getCourse().getTitle() + " Course";
        // Send in app notifications
        for (StudentInfoDTO student : enrolledStudents) {
            notificationService.createNotification(student.getStudentId(), message);
        }
        // Send mail notifications
        for (StudentInfoDTO student : enrolledStudents) {
            notificationService.sendEmailNotification(student.getEmail(), "New Assignment", message);
        }
    }
}
