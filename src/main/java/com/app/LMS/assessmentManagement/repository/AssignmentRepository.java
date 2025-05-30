package com.app.LMS.assessmentManagement.repository;

import com.app.LMS.assessmentManagement.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByCourseId(Long courseId);

    List<Assignment> findByDeadlineBetween(LocalDateTime start, LocalDateTime end);


}
