package com.app.LMS;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.apache.tomcat.util.http.parser.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.app.LMS.DTO.ProfileDTO;
import com.app.LMS.config.JwtConfig;
import com.app.LMS.userManagement.controller.ProfileController;
import com.app.LMS.userManagement.model.User;
import com.app.LMS.userManagement.service.UserService;

@WebMvcTest(ProfileController.class)
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtConfig jwtConfig;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Set up mock user data
        testUser = new User();
        testUser.setFirstName("Alaa");
        testUser.setLastName("Basuny");
        testUser.setEmail("test@example.com");
        testUser.setId(1L); // Assuming user ID is 1
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetProfileSuccess() throws Exception {
        // Mock JwtConfig to return user ID for the token
        when(jwtConfig.getUserIdFromToken(Mockito.anyString())).thenReturn(1L);
        
        // Mock UserService to return the test user
        when(userService.findById(1L)).thenReturn(Optional.of(testUser));

        // Perform GET request to view profile
        mockMvc.perform(get("/api/profile/view")
                        .header("Authorization", "Bearer validDummyToken")) // Mock a valid JWT token
                .andExpect(status().isOk()) // Expect status 200 OK
                .andExpect(jsonPath("$.firstName").value("Alaa"))
                .andExpect(jsonPath("$.lastName").value("Basuny"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetProfileUserNotFound() throws Exception {
        // Mock JwtConfig to return user ID for the token
        when(jwtConfig.getUserIdFromToken(Mockito.anyString())).thenReturn(1L);
        
        // Mock UserService to return an empty Optional (user not found)
        when(userService.findById(1L)).thenReturn(Optional.empty());

        // Perform GET request to view profile
        mockMvc.perform(get("/api/profile/view")
                        .header("Authorization", "Bearer validDummyToken")) // Mock a valid JWT token
                .andExpect(status().isNotFound()) // Expect status 404 Not Found
                .andExpect(content().string("User not found"));
    }

    
}
