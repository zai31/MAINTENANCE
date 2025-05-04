package com.app.LMS.assessmentManagement.repository;

import com.app.LMS.assessmentManagement.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    boolean existsByStudentIdAndQuizId(Long userId, Long quizId);
    List<QuizAttempt> findByStudentIdAndQuizCourseId(Long studentId, Long courseId);

}
