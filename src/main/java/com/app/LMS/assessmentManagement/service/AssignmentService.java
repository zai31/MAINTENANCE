package com.app.LMS.assessmentManagement.service;

import com.app.LMS.assessmentManagement.model.Assignment;
import com.app.LMS.assessmentManagement.repository.AssignmentRepository;
import com.app.LMS.common.Exceptions.dedicatedException;
import com.app.LMS.courseManagement.model.Course;
import com.app.LMS.courseManagement.repository.CourseRepository;
import com.app.LMS.notificationManagement.eventBus.EventBus;
import com.app.LMS.notificationManagement.eventBus.events.AssignmentDeadlineReminderEvent;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDateTime;
import java.util.List;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@org.springframework.context.annotation.Lazy
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final EventBus eventBus;


    public AssignmentService(AssignmentRepository assignmentRepository, CourseRepository courseRepository, @org.springframework.context.annotation.Lazy EventBus eventBus) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.eventBus = eventBus;
    }

    @Scheduled(cron = "0 0 * * * *") // Runs every hour
    public void checkAssignmentDeadlines() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime in24Hours = now.plusHours(24);

        // Find assignments with deadlines between now and the next 24 hours
        List<Assignment> upcomingAssignments = assignmentRepository.findByDeadlineBetween(now, in24Hours);

        for (Assignment assignment : upcomingAssignments) {
            // Publish a reminder event for each assignment
            AssignmentDeadlineReminderEvent event = new AssignmentDeadlineReminderEvent(assignment.getId());
            eventBus.publish(event);
        }
    }

    public Assignment createAssignment(Assignment assignment, Long courseId, MultipartFile file) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));

        assignment.setCourse(course);
        Assignment savedAssignment = assignmentRepository.save(assignment);

        String uploadDir = System.getProperty("user.dir") + "/uploads/courses/" + courseId + "/assignments/" + savedAssignment.getId();
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filePath = uploadDir + "/" + file.getOriginalFilename();
        try {
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            throw new dedicatedException("Failed to save file to path: " + filePath, e);
        }

        savedAssignment.setFilePath(filePath);
        return assignmentRepository.save(savedAssignment);
    }

    public List<Assignment> getAllAssignments(Long courseId) {
        return assignmentRepository.findByCourseId(courseId);
    }

    public Assignment getAssignmentById(Long assignmentId) {
        return assignmentRepository.findById(assignmentId).orElse(null);
    }
}
