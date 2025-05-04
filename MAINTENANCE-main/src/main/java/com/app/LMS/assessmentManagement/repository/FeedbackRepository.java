package com.app.LMS.assessmentManagement.repository;

import com.app.LMS.assessmentManagement.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Feedback findBySubmissionId(Long submissionId);
    List<Feedback> findBySubmissionStudentIdAndSubmissionAssignmentCourseId(Long studentId, Long courseId);

}
