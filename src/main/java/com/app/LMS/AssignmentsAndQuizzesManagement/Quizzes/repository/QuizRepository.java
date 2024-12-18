package com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.repository;

import com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
}
