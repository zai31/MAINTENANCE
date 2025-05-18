package com.app.LMS;

import com.app.LMS.DTO.CourseRequest;
import com.app.LMS.courseManagement.controller.CourseController;
import com.app.LMS.courseManagement.model.Course;
import com.app.LMS.courseManagement.service.CourseService;
import com.app.LMS.courseManagement.service.MediaService;
import com.app.LMS.courseManagement.service.LessonService;
import com.app.LMS.config.JwtConfig;
import com.app.LMS.notificationManagement.eventBus.EventBus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CourseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CourseService courseService;

    @Mock
    private MediaService mediaService;

    @Mock
    private LessonService lessonService;

    @Mock
    private JwtConfig jwtConfig;

    @Mock
    private EventBus eventBus;

    @InjectMocks
    private CourseController courseController;

    @BeforeEach
    public void setup() {

        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();


        when(jwtConfig.getRoleFromToken(anyString())).thenReturn("INSTRUCTOR");
        when(jwtConfig.getUserIdFromToken(anyString())).thenReturn(1L);
    }

    @Test
    public void testCreateCourse() throws Exception {

        CourseRequest courseRequest = new CourseRequest();
        courseRequest.setTitle("Test Course");
        courseRequest.setDescription("Test Course Description");
        courseRequest.setDuration("10 hours");

        Course createdCourse = new Course();
        createdCourse.setId(1L);
        createdCourse.setTitle("Test Course");
        createdCourse.setDescription("Test Course Description");


        when(courseService.createCourse(any(Course.class), eq(1L))).thenReturn(createdCourse);


        mockMvc.perform(post("/api/course/create")
                        .header("Authorization", "Bearer testToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(courseRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Course created successfully with ID:1"));
    }

    @Test
    public void testCreateCourseWithUnauthorizedAccess() throws Exception {
        when(jwtConfig.getRoleFromToken(anyString())).thenReturn("STUDENT");

        mockMvc.perform(post("/api/course/create")
                        .header("Authorization", "Bearer invalidToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new CourseRequest())))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Unauthorized"));
    }
}