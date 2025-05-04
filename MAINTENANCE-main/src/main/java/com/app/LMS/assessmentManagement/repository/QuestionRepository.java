package com.app.LMS.assessmentManagement.repository;

import com.app.LMS.assessmentManagement.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
