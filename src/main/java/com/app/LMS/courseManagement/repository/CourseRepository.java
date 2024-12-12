package com.app.LMS.courseManagement.repository;
import com.app.LMS.courseManagement.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
