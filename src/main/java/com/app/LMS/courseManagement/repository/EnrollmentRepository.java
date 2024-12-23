package com.app.LMS.courseManagement.repository;
import com.app.LMS.courseManagement.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByCourseId(Long courseId);
    boolean existsByCourse_IdAndStudent_Id(Long courseId, Long studentId);

}
