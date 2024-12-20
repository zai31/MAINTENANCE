package com.app.LMS.assessmentManagement.repository;

import com.app.LMS.assessmentManagement.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
}
