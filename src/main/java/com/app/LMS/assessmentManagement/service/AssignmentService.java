package com.app.LMS.assessmentManagement.service;

import com.app.LMS.assessmentManagement.model.Assignment;
import com.app.LMS.assessmentManagement.repository.AssignmentRepository;
import com.app.LMS.common.Exceptions.dedicatedException;
import com.app.LMS.courseManagement.model.Course;
import com.app.LMS.courseManagement.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;


    public AssignmentService(AssignmentRepository assignmentRepository, CourseRepository courseRepository) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;

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
