package com.app.LMS.assessmentManagement.repository;

import com.app.LMS.assessmentManagement.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
}
