package com.app.LMS.assessmentManagement.service;

import com.app.LMS.assessmentManagement.model.Assignment;
import com.app.LMS.assessmentManagement.repository.AssignmentRepository;
import com.app.LMS.courseManagement.model.Course;
import com.app.LMS.courseManagement.repository.CourseRepository;
import org.springframework.stereotype.Service;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;

    public AssignmentService(AssignmentRepository assignmentRepository, CourseRepository courseRepository) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
    }

    public Assignment createAssignment(Assignment assignment, Long instructorId) {
        // Check if the course exists
        Course course = courseRepository.findById(assignment.getCourse().getId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if the instructor is authorized
        if (!course.getInstructor().getId().equals(instructorId)) {
            throw new RuntimeException("Unauthorized to create assignment for this course");
        }

        assignment.setCourse(course);
        return assignmentRepository.save(assignment);
    }
}
