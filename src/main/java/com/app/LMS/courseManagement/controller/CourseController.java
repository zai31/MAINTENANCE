package com.app.LMS.courseManagement.controller;

import java.util.List;
import com.app.LMS.DTO.*;
import com.app.LMS.common.Constants;
import com.app.LMS.common.Exceptions.dedicatedException;
import com.app.LMS.courseManagement.service.LessonService;
import com.app.LMS.notificationManagement.eventBus.EventBus;
import com.app.LMS.notificationManagement.eventBus.events.AddedLessonEvent;
import com.app.LMS.notificationManagement.eventBus.events.MaterialUploadedEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import com.app.LMS.config.JwtConfig;
import com.app.LMS.courseManagement.service.CourseService;
import com.app.LMS.courseManagement.service.MediaService;
import com.app.LMS.courseManagement.model.Course;
import com.app.LMS.courseManagement.model.Lesson;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/course")
public class CourseController {
    private final CourseService courseService;
    @Autowired
    private final MediaService mediaService;
    private final JwtConfig jwtConfig;
    private final LessonService lessonService;
    private final EventBus eventBus;

    private boolean isInstructorAuthorized(String role, Long instructorId, Long courseId) {
        if (!Constants.ROLE_INSTRUCTOR.equals(role)) {
            return true;
        }
        Course course = courseService.findCourseById(courseId);
        return course != null && course.getInstructor().getId().equals(instructorId);
    }

    public CourseController(CourseService courseService, MediaService mediaservice, JwtConfig jwtConfig, LessonService lessonService, EventBus eventBus)
    {
        this.courseService = courseService;
        this.mediaService = mediaservice;
        this.jwtConfig = jwtConfig;
        this.lessonService = lessonService;
        this.eventBus = eventBus;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createCourse(@RequestHeader("Authorization") String token, @RequestBody @Valid CourseRequest courseRequest) {
        try
        {
            String role = jwtConfig.getRoleFromToken(token);
            if (Constants.ROLE_INSTRUCTOR.equals(role)) {
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
                return new ResponseEntity<>(Constants.UNAUTHORIZED, HttpStatus.FORBIDDEN);
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
            if (Constants.ROLE_INSTRUCTOR.equals(role)) {
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
                return new ResponseEntity<>(Constants.UNAUTHORIZED, HttpStatus.FORBIDDEN);
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
            if (!Constants.ROLE_INSTRUCTOR.equals(role) && !Constants.ROLE_ADMIN.equals(role)) {
                return new ResponseEntity<>(Constants.UNAUTHORIZED, HttpStatus.FORBIDDEN);
            }

            // Retrieve the course from the database
            Course course = courseService.findCourseById(courseId);
            if (course == null) {
                throw new dedicatedException.CourseNotFoundException("Course not found with id: " + courseId);
            }

            if (Constants.ROLE_INSTRUCTOR.equals(role) && !course.getInstructor().getId().equals(instructorId)) {
                return new ResponseEntity<>(Constants.UNAUTHORIZED_COURSE_ACCESS, HttpStatus.FORBIDDEN);
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
        } catch (dedicatedException.CourseNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (dedicatedException.UnauthorizedActionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
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
            if (!Constants.ROLE_INSTRUCTOR.equals(role) && !Constants.ROLE_ADMIN.equals(role)) {
                return new ResponseEntity<>(Constants.UNAUTHORIZED, HttpStatus.FORBIDDEN);
            }

            // Retrieve the course from the database
            Course course = courseService.findCourseById(courseId);
            if (course == null) {
                throw new dedicatedException.CourseNotFoundException("Course not found with id: " + courseId);
            }

            // Check if the instructor owns the course
            if (Constants.ROLE_INSTRUCTOR.equals(role) && !course.getInstructor().getId().equals(instructorId)) {
                return new ResponseEntity<>(Constants.UNAUTHORIZED_COURSE_ACCESS, HttpStatus.FORBIDDEN);
            }

            // Delete the course
            courseService.delete(course);

            return new ResponseEntity<>("Course deleted successfully", HttpStatus.OK);
        } catch (dedicatedException.CourseNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (dedicatedException.UnauthorizedActionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting course: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{courseId}/lesson/create")
    public ResponseEntity<String> addLesson(@RequestHeader("Authorization") String token, @PathVariable Long courseId, @RequestBody @Valid LessonRequest lessonRequest) {
        try {
            // Extract role and validate authorization
            String role = jwtConfig.getRoleFromToken(token);
            if (Constants.ROLE_INSTRUCTOR.equals(role)) {

                // Retrieve the instructor ID from the JWT token
                Long instructorId = jwtConfig.getUserIdFromToken(token);

                // Retrieve the course from the database using courseId
                Course course = courseService.findCourseById(courseId);
                if (course == null) {
                    throw new dedicatedException.CourseNotFoundException("Course not found with id: " + courseId);
                }

                // Check if the current instructor owns the course
                if (!course.getInstructor().getId().equals(instructorId)) {
                    return new ResponseEntity<>(Constants.UNAUTHORIZED_COURSE_ACCESS, HttpStatus.FORBIDDEN);
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
                return new ResponseEntity<>(Constants.UNAUTHORIZED, HttpStatus.FORBIDDEN);
            }
        } catch (dedicatedException.CourseNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (dedicatedException.UnauthorizedActionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
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
            if (!Constants.ROLE_INSTRUCTOR.equals(role) && !Constants.ROLE_ADMIN.equals(role)) {
                return new ResponseEntity<>(Constants.UNAUTHORIZED, HttpStatus.FORBIDDEN);
            }

            // Retrieve the course and lesson
            Course course = courseService.findCourseById(courseId);
            if (course == null) {
                throw new dedicatedException.CourseNotFoundException("Course not found with id: " + courseId);
            }

            Lesson lesson = lessonService.getByID(lessonId);
            if (lesson == null) {
                throw new dedicatedException.LessonNotFoundException("Lesson not found with id: " + lessonId);
            }

            // Check if the instructor owns the course
            if (!Constants.ROLE_ADMIN.equals(role) && !isInstructorAuthorized(role, instructorId, courseId)) {
                return new ResponseEntity<>(Constants.UNAUTHORIZED_COURSE_ACCESS, HttpStatus.FORBIDDEN);
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
        } catch (dedicatedException.LessonNotFoundException | dedicatedException.CourseNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (dedicatedException.UnauthorizedActionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
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
            if (!Constants.ROLE_INSTRUCTOR.equals(role) && !Constants.ROLE_ADMIN.equals(role)) {
                return new ResponseEntity<>(Constants.UNAUTHORIZED, HttpStatus.FORBIDDEN);
            }

            Lesson lesson = lessonService.getByID(lessonId);
            if (lesson == null) {
                throw new dedicatedException.LessonNotFoundException("Lesson not found with id: " + lessonId);
            }

            // Check if the instructor owns the course
            if (!Constants.ROLE_ADMIN.equals(role) && !isInstructorAuthorized(role, instructorId, courseId)) {
                return new ResponseEntity<>(Constants.UNAUTHORIZED_COURSE_ACCESS, HttpStatus.FORBIDDEN);
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

            if (!Constants.ROLE_INSTRUCTOR.equals(role) && !Constants.ROLE_ADMIN.equals(role)) {
                return new ResponseEntity<>(Constants.UNAUTHORIZED, HttpStatus.FORBIDDEN);
            }

            Lesson lesson = lessonService.getByID(lessonId);
            if (!Constants.ROLE_ADMIN.equals(role) && !isInstructorAuthorized(role, instructorId, lesson.getCourse().getId())) {
                return new ResponseEntity<>(Constants.UNAUTHORIZED_COURSE_ACCESS, HttpStatus.FORBIDDEN);
            }

            // Use MediaService to handle the file upload
            String response = mediaService.uploadFile(lessonId, file);

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

    @GetMapping("/lesson/{id}/content")
    public ResponseEntity<LessonContentDTO> getLessonContent(@RequestHeader("Authorization") String token,
                                                             @PathVariable Long id) {
        String role = jwtConfig.getRoleFromToken(token);
        Long studentId = jwtConfig.getUserIdFromToken(token);

        Course course = lessonService.getByID(id).getCourse();
        boolean enrolled = courseService.isEnrolled(course.getId(), studentId);

        if (Constants.ROLE_STUDENT.equals(role) && enrolled) {
            LessonContentDTO lesson = courseService.getLesson(id);
            return ResponseEntity.ok(lesson);
        }
        throw new dedicatedException.UnauthorizedActionException("Only enrolled students can view lesson content.");
    }





}