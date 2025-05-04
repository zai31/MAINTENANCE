package com.app.LMS.courseManagement.repository;
import com.app.LMS.courseManagement.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
}
