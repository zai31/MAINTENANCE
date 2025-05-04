package com.app.LMS.courseManagement.controller;
import java.util.List;
import com.app.LMS.DTO.*;
import com.app.LMS.courseManagement.service.LessonService;
import com.app.LMS.notificationManagement.eventBus.EventBus;
import com.app.LMS.notificationManagement.eventBus.events.AddedLessonEvent;
import com.app.LMS.notificationManagement.eventBus.events.EnrollmentEvent;
import com.app.LMS.notificationManagement.eventBus.events.MaterialUploadedEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.app.LMS.config.JwtConfig;
import com.app.LMS.courseManagement.model.Course;
import com.app.LMS.courseManagement.model.Lesson;
import com.app.LMS.courseManagement.service.CourseService;
import com.app.LMS.courseManagement.service.MediaService;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/course")
public class CourseController {
    private final CourseService courseService;
    private final MediaService mediaservice;
    private final JwtConfig jwtConfig;
    private final LessonService lessonService;
    private final EventBus eventBus;
    public CourseController(CourseService courseService, MediaService mediaservice, JwtConfig jwtConfig, LessonService lessonService, EventBus eventBus)
    {
        this.courseService = courseService;
        this.mediaservice = mediaservice;
        this.jwtConfig = jwtConfig;
        this.lessonService = lessonService;
        this.eventBus = eventBus;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createCourse(@RequestHeader("Authorization") String token, @RequestBody @Valid CourseRequest courseRequest) {
        try
        {
            String role = jwtConfig.getRoleFromToken(token);
            if ("INSTRUCTOR".equals(role)) {
                Course course = new Course();
                course.setTitle(courseRequest.getTitle());
                course.setDescription(courseRequest.getDescription());
                course.setDuration(courseRequest.getDuration());

                Long instructorId = jwtConfig.getUserIdFromToken(token);
                Course createdCourse = courseService.createCourse(course, instructorId);

                return new ResponseEntity<>("Course created successfully with ID:" + createdCourse.getId(), HttpStatus.CREATED);
            }
            else
            {
                return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
            }
        }
        catch (Exception e) {
            return new ResponseEntity<>("Error creating course: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/admin/create")
    public ResponseEntity<String> createCourseByAdmin(@RequestHeader("Authorization") String token, @RequestBody @Valid Courseresponse courseRequest) {
    
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
                Course createdCourse = courseService.createCourse(course, instructorId);
    
                return new ResponseEntity<>("Course created successfully with ID:" + createdCourse.getId(), HttpStatus.CREATED);
            }
            else
            {
                return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating course: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{courseId}")
    public ResponseEntity<String> editCourse(@RequestHeader("Authorization") String token, @PathVariable Long courseId, @RequestBody @Valid CourseRequest courseRequest) {
        try {
            // Extract role and instructor's ID from the token
            String role = jwtConfig.getRoleFromToken(token);
            Long instructorId = jwtConfig.getUserIdFromToken(token);

            // Ensure that the role is INSTRUCTOR
            if (!"INSTRUCTOR".equals(role) && !"ADMIN".equals(role)) {
                return new ResponseEntity<>("Unauthorized: You need to be an Instructor or an Admin", HttpStatus.FORBIDDEN);
            }

            // Retrieve the course from the database
            Course course = courseService.findCourseById(courseId);

            if ("INSTRUCTOR".equals(role)) {
                // Check if the instructor owns the course
                if (!course.getInstructor().getId().equals(instructorId)) {
                    return new ResponseEntity<>("Unauthorized: You are not the owner of this course", HttpStatus.FORBIDDEN);
                }
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
            courseService.saveCourse(course);

            return new ResponseEntity<>("Course updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error editing course: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/{courseId}")
    public ResponseEntity<String> deleteCourse(@RequestHeader("Authorization") String token, @PathVariable Long courseId) {
        try {
            // Extract role and instructor's ID from the token
            String role = jwtConfig.getRoleFromToken(token);
            Long instructorId = jwtConfig.getUserIdFromToken(token);

            // Ensure that the role is INSTRUCTOR
            if (!"INSTRUCTOR".equals(role) && !"ADMIN".equals(role)) {
                return new ResponseEntity<>("Unauthorized: You need to be an Instructor or an Admin", HttpStatus.FORBIDDEN);
            }

            // Retrieve the course from the database
            Course course = courseService.findCourseById(courseId);

            // Check if the instructor owns the course
            if ("INSTRUCTOR".equals(role)) {
                // Check if the instructor owns the course
                if (!course.getInstructor().getId().equals(instructorId)) {
                    return new ResponseEntity<>("Unauthorized: You are not the owner of this course", HttpStatus.FORBIDDEN);
                }
            }

            // Delete the course
            courseService.delete(course);

            return new ResponseEntity<>("Course deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting course: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{courseId}/lesson/create")
    public ResponseEntity<String> addLesson(@RequestHeader("Authorization") String token, @PathVariable Long courseId, @RequestBody @Valid LessonRequest lessonRequest) {
        try {
            // Extract role and validate authorization
            String role = jwtConfig.getRoleFromToken(token);
            if ("INSTRUCTOR".equals(role)) {
    
                // Retrieve the instructor ID from the JWT token
                Long instructorId = jwtConfig.getUserIdFromToken(token);
    
                // Retrieve the course from the database using courseId
                Course course = courseService.findCourseById(courseId);
    
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
                Lesson created = lessonService.saveLesson(lesson);
                if(created != null)
                {
                    AddedLessonEvent event = new AddedLessonEvent(created.getCourse().getId());
                    eventBus.publish(event);
                }
                return new ResponseEntity<>("Lesson created successfully with ID: " + created.getId(), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error adding lesson: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PatchMapping("/{courseId}/lesson/{lessonId}")
    public ResponseEntity<String> editLesson(@RequestHeader("Authorization") String token, @PathVariable Long courseId, @PathVariable Long lessonId, @RequestBody @Valid LessonRequest lessonRequest) {
        try {
            // Extract role and validate authorization
            String role = jwtConfig.getRoleFromToken(token);
            Long instructorId = jwtConfig.getUserIdFromToken(token);
            
            // Ensure the user has the right role
            if (!"INSTRUCTOR".equals(role) && !"ADMIN".equals(role)) {
                return new ResponseEntity<>("Unauthorized: You must be an instructor or admin", HttpStatus.FORBIDDEN);
            }
            
            // Retrieve the course and lesson
            Course course = courseService.findCourseById(courseId);
            
            Lesson lesson = lessonService.getByID(lessonId);
            
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
            lessonService.save(lesson);
            
            return new ResponseEntity<>("Lesson updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error editing lesson: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/{courseId}/lesson/{lessonId}")
    public ResponseEntity<String> deleteLesson(@RequestHeader("Authorization") String token, @PathVariable Long courseId, @PathVariable Long lessonId) {
        try {
            // Extract role and validate authorization
            String role = jwtConfig.getRoleFromToken(token);
            Long instructorId = jwtConfig.getUserIdFromToken(token);

            // Ensure the user has the right role
            if (!"INSTRUCTOR".equals(role) && !"ADMIN".equals(role)) {
                return new ResponseEntity<>("Unauthorized: You must be an instructor or admin", HttpStatus.FORBIDDEN);
            }

            // Retrieve the course and lesson
            Course course = courseService.findCourseById(courseId);

            Lesson lesson = lessonService.getByID(lessonId);

            // Check if the instructor owns the course
            if ("INSTRUCTOR".equals(role)) {
                if (!course.getInstructor().getId().equals(instructorId)) {
                    return new ResponseEntity<>("Unauthorized: You do not own this course", HttpStatus.FORBIDDEN);
                }
            }


            // Delete the lesson
            lessonService.delete(lesson);

            return new ResponseEntity<>("Lesson deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting lesson: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   

    @PostMapping(value="/{lessonId}/uploadMedia", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadMedia(@RequestHeader("Authorization") String token, @PathVariable Long lessonId, @RequestParam("file") MultipartFile file) {
        try {
            // Extract role and validate authorization
            String role = jwtConfig.getRoleFromToken(token);
            Long instructorId = jwtConfig.getUserIdFromToken(token);

            if (!"INSTRUCTOR".equals(role) && !"ADMIN".equals(role)) {
                return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
            }

            Lesson lesson = lessonService.getByID(lessonId);
            if ("INSTRUCTOR".equals(role)) {
                if (!lesson.getCourse().getInstructor().getId().equals(instructorId)) {
                    return new ResponseEntity<>("Unauthorized: You do not own this course", HttpStatus.FORBIDDEN);
                }
            }

            // Use MediaService to handle the file upload
            String response = mediaservice.uploadFile(lessonId, file);

            if ("Lesson not found".equals(response)) {
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else if (response.startsWith("Error uploading file")) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            MaterialUploadedEvent event = new MaterialUploadedEvent(lessonId);
            eventBus.publish(event);

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
    public ResponseEntity<String> enrollStudentInCourse(@RequestHeader("Authorization") String token, @PathVariable Long courseId) {
        try {
            // Extract the student ID and role from the token
            Long studentId = jwtConfig.getUserIdFromToken(token);
            String role = jwtConfig.getRoleFromToken(token);

            // Check if the role is STUDENT
            if (!"STUDENT".equals(role)) {
                return ResponseEntity.status(403).body("Only students are allowed to enroll in courses");
            }

            // Enroll the student in the course
            Boolean enrolled = courseService.enrollStudentInCourse(courseId, studentId);
            if(enrolled){
                EnrollmentEvent event = new EnrollmentEvent(studentId, courseId);
                eventBus.publish(event);
            }
            return ResponseEntity.ok("Student enrolled successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error enrolling in course: " + e.getMessage());
        }
    }

    @GetMapping("/{courseId}/enrolled-students")
    public ResponseEntity<?> viewEnrolledStudents(@RequestHeader("Authorization") String token, @PathVariable Long courseId) {
        try {
            // Extract user role from the token
            String role = jwtConfig.getRoleFromToken(token);
            Long instructorId = jwtConfig.getUserIdFromToken(token);

            // Only Admins and Instructors can access this endpoint
            if (!"ADMIN".equals(role) && !"INSTRUCTOR".equals(role)) {
                return ResponseEntity.status(403).body("Unauthorized");
            }

            Course course = courseService.findCourseById(courseId);
            if(!course.getInstructor().getId().equals(instructorId)){
                return ResponseEntity.status(403).body("Unauthorized: You do not own this course");
            }

            // Get the list of enrolled students
            List<StudentInfoDTO> enrolledStudents = courseService.getEnrolledStudents(courseId);

            return ResponseEntity.ok(enrolledStudents);
        }
        catch (Exception e) {
            return ResponseEntity.status(400).body("Error fetching enrolled students: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<?> getContent(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        String role = jwtConfig.getRoleFromToken(token);
        Long studentId = jwtConfig.getUserIdFromToken(token);
        if (!"STUDENT".equals(role)) {
            return ResponseEntity.status(403).body("Only students are allowed to get the content of a course");
        }
        boolean enrolled = courseService.isEnrolled(id, studentId);
        if(!enrolled){
            return ResponseEntity.status(403).body("You must be enrolled in the course to be able to view its content");
        }

        CourseContentDTO course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    @GetMapping("/lesson/{id}/content")
    public ResponseEntity<?> getLessonContent(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        String role = jwtConfig.getRoleFromToken(token);
        Long studentId = jwtConfig.getUserIdFromToken(token);
        if (!"STUDENT".equals(role)) {
            return ResponseEntity.status(403).body("Only students are allowed to get the content of a lesson");
        }
        Course course = lessonService.getByID(id).getCourse();
        boolean enrolled = courseService.isEnrolled(course.getId(), studentId);
        if(!enrolled){
            return ResponseEntity.status(403).body("You must be enrolled in the course to be able to view its content");
        }
        LessonContentDTO lesson = courseService.getLesson(id);
        return ResponseEntity.ok(lesson);
    }

}