package com.app.LMS.courseManagement.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.app.LMS.courseManagement.model.Lesson;
import com.app.LMS.courseManagement.repository.LessonRepository;

@Service
public class MediaService {

    private final LessonRepository lessonRepository;

    public MediaService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    // Method to upload file for an existing course
    public String uploadFile(Long lessonId, MultipartFile file) {
        // Retrieve the lesson by ID
        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
        if (lesson == null) {
            return "Lesson not found";
        }

        // Set the upload directory
        String uploadDir = System.getProperty("user.dir") + "/uploads/lessons/" + lesson.getId();
        File directory = new File(uploadDir);
        if (!directory.exists() && !directory.mkdirs()) {
            return "Failed to create upload directory";
        }

        try {
            // Save the file to the directory
            Path filePath = Paths.get(uploadDir, file.getOriginalFilename());
            System.out.println("File will be uploaded to: " + filePath.toString()); // Debugging
            file.transferTo(filePath.toFile()); // Ensure compatibility

            // Add the file path to the lesson's mediaPaths
            if (lesson.getMediaPaths() == null) {
                lesson.setMediaPaths(new ArrayList<>()); // Initialize if null
            }
            lesson.getMediaPaths().add(filePath.toString());

            // Save the lesson with the new media path
            lessonRepository.save(lesson);

            return "File uploaded successfully";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error uploading file: " + e.getMessage();
        }
    }


}
