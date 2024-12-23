package com.app.LMS.assessmentManagement.repository;

import com.app.LMS.assessmentManagement.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByAssignmentId(Long assignmentId);
    List<Submission> findByStudent_Id(Long studentId);

}
