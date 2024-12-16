package com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.repository;

import com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
