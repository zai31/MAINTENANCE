package com.app.LMS.courseManagement.controller;
import java.util.List;

import com.app.LMS.DTO.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.LMS.config.JwtConfig;
import com.app.LMS.courseManagement.model.Course;
import com.app.LMS.courseManagement.model.Lesson;
import com.app.LMS.courseManagement.repository.CourseRepository;
import com.app.LMS.courseManagement.repository.LessonRepository;
import com.app.LMS.courseManagement.service.CourseService;
import com.app.LMS.courseManagement.service.MediaService;
import com.app.LMS.userManagement.repository.UserRepository;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/course")

public class CourseController {
    private final CourseService courseService;
    private final MediaService mediaservice;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final JwtConfig jwtConfig;

    public CourseController(CourseService courseService, MediaService mediaservice, UserRepository userRepository, LessonRepository lessonRepository, CourseRepository courseRepository, JwtConfig jwtConfig)
    {
        this.courseService = courseService;
        this.mediaservice = mediaservice;
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
        this.courseRepository = courseRepository;
        this.jwtConfig = jwtConfig;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createCourse(@RequestHeader("Authorization") String token, @RequestBody
    @Valid CourseRequest courseRequest) {
        try {
            String role = jwtConfig.getRoleFromToken(token);
            if ("INSTRUCTOR".equals(role)) {
                Course course = new Course();
                course.setTitle(courseRequest.getTitle());
                course.setDescription(courseRequest.getDescription());
                course.setDuration(courseRequest.getDuration());

                Long instructorId = jwtConfig.getUserIdFromToken(token);
                courseService.createCourse(course, instructorId);

                return new ResponseEntity<>("Course created successfully", HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating course: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/createByAdmin")
    public ResponseEntity<String> createCourseByAdmin(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid Courseresponse courseRequest) {
    
        try {
            // Check if the role is "ADMIN"
            String role = jwtConfig.getRoleFromToken(token);
            if ("ADMIN".equals(role)) {
                Course course = new Course();
                course.setTitle(courseRequest.getTitle());
                course.setDescription(courseRequest.getDescription());
                course.setDuration(courseRequest.getDuration());
    
                // Admin provides the instructorId in the request body
                Long instructorId = courseRequest.getInstructorId();
                courseService.createCourse(course, instructorId);
    
                return new ResponseEntity<>("Course created successfully by Admin", HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating course: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    



    

@PutMapping("/{courseId}/edit")
public ResponseEntity<String> editCourse(
        @RequestHeader("Authorization") String token,
        @PathVariable Long courseId,
        @RequestBody @Valid CourseRequest courseRequest) {
    try {
        // Extract role and instructor's ID from the token
        String role = jwtConfig.getRoleFromToken(token);
        Long instructorId = jwtConfig.getUserIdFromToken(token);

        // Ensure that the role is INSTRUCTOR
        if (!"INSTRUCTOR".equals(role)) {
            return new ResponseEntity<>("Unauthorized: You need to be an instructor", HttpStatus.FORBIDDEN);
        }

        // Retrieve the course from the database
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if the instructor owns the course
        if (!course.getInstructor().getId().equals(instructorId)) {
            return new ResponseEntity<>("Unauthorized: You are not the owner of this course", HttpStatus.FORBIDDEN);
        }

        // Update the course details only for the provided fields
        if (courseRequest.getTitle() != null) {
            course.setTitle(courseRequest.getTitle());
        }
        if (courseRequest.getDescription() != null) {
            course.setDescription(courseRequest.getDescription());
        }
        if (courseRequest.getDuration() != null) {
            course.setDuration(courseRequest.getDuration());
        }

        // Save the updated course
        courseRepository.save(course);

        return new ResponseEntity<>("Course updated successfully", HttpStatus.OK);
    } catch (Exception e) {
        return new ResponseEntity<>("Error editing course: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


@DeleteMapping("/{courseId}/delete")
public ResponseEntity<String> deleteCourse(
        @RequestHeader("Authorization") String token,
        @PathVariable Long courseId) {
    try {
        // Extract role and instructor's ID from the token
        String role = jwtConfig.getRoleFromToken(token);
        Long instructorId = jwtConfig.getUserIdFromToken(token);

        // Ensure that the role is INSTRUCTOR
        if (!"INSTRUCTOR".equals(role)) {
            return new ResponseEntity<>("Unauthorized: You need to be an instructor", HttpStatus.FORBIDDEN);
        }

        // Retrieve the course from the database
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if the instructor owns the course
        if (!course.getInstructor().getId().equals(instructorId)) {
            return new ResponseEntity<>("Unauthorized: You are not the owner of this course", HttpStatus.FORBIDDEN);
        }

        // Delete the course
        courseRepository.delete(course);

        return new ResponseEntity<>("Course deleted successfully", HttpStatus.OK);
    } catch (Exception e) {
        return new ResponseEntity<>("Error deleting course: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
    @PostMapping("/{courseId}/addLesson")
    public ResponseEntity<String> addLesson(
            @RequestHeader("Authorization") String token,
            @PathVariable Long courseId,
            @RequestBody @Valid LessonRequest lessonRequest) {
        try {
            // Extract role and validate authorization
            String role = jwtConfig.getRoleFromToken(token);
            if ("INSTRUCTOR".equals(role) || "ADMIN".equals(role)) {
    
                // Retrieve the instructor ID from the JWT token
                Long instructorId = jwtConfig.getUserIdFromToken(token);
    
                // Retrieve the course from the database using courseId
                Course course = courseRepository.findById(courseId)
                        .orElseThrow(() -> new RuntimeException("Course not found"));
    
                // Check if the current instructor owns the course
                if (!course.getInstructor().getId().equals(instructorId)) {
                    return new ResponseEntity<>("Unauthorized: You do not own this course", HttpStatus.FORBIDDEN);
                }
    
                // Create a new Lesson entity from the request
                Lesson lesson = new Lesson();
                lesson.setTitle(lessonRequest.getTitle());
                lesson.setContent(lessonRequest.getContent());
    
                // Set the course for the lesson
                lesson.setCourse(course);
    
                // Save the lesson to the database
                lessonRepository.save(lesson);
    
                return new ResponseEntity<>("Lesson added successfully", HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error adding lesson: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/{courseId}/editLesson/{lessonId}")
    public ResponseEntity<String> editLesson(
            @RequestHeader("Authorization") String token,
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @RequestBody @Valid LessonRequest lessonRequest) {
        try {
            // Extract role and validate authorization
            String role = jwtConfig.getRoleFromToken(token);
            Long instructorId = jwtConfig.getUserIdFromToken(token);
            
            // Ensure the user has the right role
            if (!"INSTRUCTOR".equals(role) && !"ADMIN".equals(role)) {
                return new ResponseEntity<>("Unauthorized: You must be an instructor or admin", HttpStatus.FORBIDDEN);
            }
            
            // Retrieve the course and lesson
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));
            
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new RuntimeException("Lesson not found"));
            
            // Check if the instructor owns the course
            if (!course.getInstructor().getId().equals(instructorId)) {
                return new ResponseEntity<>("Unauthorized: You do not own this course", HttpStatus.FORBIDDEN);
            }
            
            // Update the lesson details based on the provided request body
            if (lessonRequest.getTitle() != null) {
                lesson.setTitle(lessonRequest.getTitle());
            }
            if (lessonRequest.getContent() != null) {
                lesson.setContent(lessonRequest.getContent());
            }
            
            // Save the updated lesson
            lessonRepository.save(lesson);
            
            return new ResponseEntity<>("Lesson updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error editing lesson: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    

@DeleteMapping("/{courseId}/deleteLesson/{lessonId}")
public ResponseEntity<String> deleteLesson(
        @RequestHeader("Authorization") String token,
        @PathVariable Long courseId,
        @PathVariable Long lessonId) {
    try {
        // Extract role and validate authorization
        String role = jwtConfig.getRoleFromToken(token);
        Long instructorId = jwtConfig.getUserIdFromToken(token);

        // Ensure the user has the right role
        if (!"INSTRUCTOR".equals(role) && !"ADMIN".equals(role)) {
            return new ResponseEntity<>("Unauthorized: You must be an instructor or admin", HttpStatus.FORBIDDEN);
        }

        // Retrieve the course and lesson
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        // Check if the instructor owns the course
        if (!course.getInstructor().getId().equals(instructorId)) {
            return new ResponseEntity<>("Unauthorized: You do not own this course", HttpStatus.FORBIDDEN);
        }

        // Delete the lesson
        lessonRepository.delete(lesson);

        return new ResponseEntity<>("Lesson deleted successfully", HttpStatus.OK);
    } catch (Exception e) {
        return new ResponseEntity<>("Error deleting lesson: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

   

    @PostMapping("/{lessonId}/uploadMedia")
    public ResponseEntity<String> uploadMedia(
            @RequestHeader("Authorization") String token,
            @PathVariable Long lessonId,
            @RequestParam("file") MultipartFile file) {
        try {
            // Extract role and validate authorization
            String role = jwtConfig.getRoleFromToken(token);
            if (!"INSTRUCTOR".equals(role) && !"ADMIN".equals(role)) {
                return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
            }

            // Use MediaService to handle the file upload
            String response = mediaservice.uploadFile(lessonId, file);

            if ("Lesson not found".equals(response)) {
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else if (response.startsWith("Error uploading file")) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



   


    // Endpoint for students to view all available courses
    @GetMapping("/available")
    public ResponseEntity<List<CourseRequest>> getAllCourses() {
        List<CourseRequest> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);  // Return available courses as CourseResponse
    }






    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<String> enrollStudentInCourse(
            @RequestHeader("Authorization") String token,
            @PathVariable Long courseId) {
        try {
            // Extract the student ID and role from the token
            Long studentId = jwtConfig.getUserIdFromToken(token);
            String role = jwtConfig.getRoleFromToken(token);

            // Check if the role is STUDENT
            if (!"STUDENT".equals(role)) {
                return ResponseEntity.status(403).body("Only students are allowed to enroll in courses");
            }

            // Enroll the student in the course
            courseService.enrollStudentInCourse(courseId, studentId);

            return ResponseEntity.ok("Student enrolled successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error enrolling in course: " + e.getMessage());
        }
    }

    @GetMapping("/{courseId}/enrolled-students")
    public ResponseEntity<?> viewEnrolledStudents(
            @RequestHeader("Authorization") String token,
            @PathVariable Long courseId) {
        try {
            // Extract user role from the token
            String role = jwtConfig.getRoleFromToken(token);

            // Only Admins and Instructors can access this endpoint
            if (!"ADMIN".equals(role) && !"INSTRUCTOR".equals(role)) {
                return ResponseEntity.status(403).body("Unauthorized");
            }

            // Get the list of enrolled students
            List<String> enrolledStudents = courseService.getEnrolledStudents(courseId);

            return ResponseEntity.ok(enrolledStudents);
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body("Error fetching enrolled students: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<CourseContentDTO> getContent(@PathVariable Long id) {
        CourseContentDTO course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    @GetMapping("/lesson/{id}/content")
    public ResponseEntity<LessonContentDTO> getLessonContent(@PathVariable Long id) {
        LessonContentDTO lesson = courseService.getLesson(id);
        return ResponseEntity.ok(lesson);
    }

}