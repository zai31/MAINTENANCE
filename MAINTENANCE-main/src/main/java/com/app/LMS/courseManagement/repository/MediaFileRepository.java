package com.app.LMS.courseManagement.repository;

import com.app.LMS.courseManagement.model.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {
}
