package com.app.LMS;

import com.app.LMS.DTO.CourseRequest;
import com.app.LMS.DTO.Courseresponse;
import com.app.LMS.courseManagement.controller.CourseController;
import com.app.LMS.courseManagement.model.Course;
import com.app.LMS.courseManagement.service.CourseService;
import com.app.LMS.courseManagement.service.MediaService;
import com.app.LMS.courseManagement.service.LessonService;
import com.app.LMS.config.JwtConfig;
import com.app.LMS.notificationManagement.eventBus.EventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @MockBean
    private MediaService mediaService;

    @MockBean
    private LessonService lessonService;

    @MockBean
    private JwtConfig jwtConfig;

    @MockBean
    private EventBus eventBus;

    @BeforeEach
    public void setup() {
        // Mock JWT behavior to return valid role and user ID for "INSTRUCTOR"
        when(jwtConfig.getRoleFromToken(anyString())).thenReturn("INSTRUCTOR");
        when(jwtConfig.getUserIdFromToken(anyString())).thenReturn(1L);
    }

    @Test
    @WithMockUser(username = "testuser", roles = "INSTRUCTOR")
    public void testCreateCourse() throws Exception {
        // Create the CourseRequest object to send in the request body
        CourseRequest courseRequest = new CourseRequest();
        courseRequest.setTitle("Test Course");
        courseRequest.setDescription("Test Course Description");
        courseRequest.setDuration("10 hours");

        // Mock the created Course object that will be returned by the service
        Course createdCourse = new Course();
        createdCourse.setId(1L);
        createdCourse.setTitle("Test Course");
        createdCourse.setDescription("Test Course Description");

        // Mock the service layer to return the created course
        when(courseService.createCourse(any(Course.class), eq(1L))).thenReturn(createdCourse);

        // Perform the POST request and validate the response
        mockMvc.perform(post("/api/course/create")
                .header("Authorization", "Bearer testToken")  // Include the JWT token
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Test Course\", \"description\": \"Test Course Description\", \"duration\": \"10 hours\"}"))
                .andExpect(status().isCreated())  // Assert that the status code is 201 (Created)
                .andExpect(content().string("Course created successfully with ID:1"));  // Assert the success message
    }
    @Test
    @WithMockUser(username = "testuser", roles = "STUDENT")  // Simulate unauthorized role (STUDENT)
    public void testCreateCourseWithUnauthorizedAccess() throws Exception {
        // Mocking JWT to return "STUDENT" role
        when(jwtConfig.getRoleFromToken(anyString())).thenReturn("STUDENT");  // Mocking the role to be "STUDENT"
    
        // Perform the POST request with the "STUDENT" role and invalid token
        mockMvc.perform(post("/api/course/create")
                .header("Authorization", "Bearer invalidToken")  // Passing an invalid token (not used for role, just for test context)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Test Course\", \"description\": \"Test Course Description\", \"duration\": \"10 hours\"}"))
                .andExpect(status().isForbidden())  // Expect 403 Forbidden due to lack of "INSTRUCTOR" role
                .andExpect(content().string("Unauthorized"));  // Assert the "Unauthorized" message is returned from the controller
    }

    
}
