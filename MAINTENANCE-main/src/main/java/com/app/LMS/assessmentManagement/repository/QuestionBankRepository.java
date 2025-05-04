package com.app.LMS.assessmentManagement.repository;

import com.app.LMS.assessmentManagement.model.QuestionBank;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface QuestionBankRepository extends JpaRepository<QuestionBank, Long> {
    Optional<QuestionBank> findByCourseId(Long courseId);
}
