package com.app.LMS;
import com.app.LMS.DTO.SignInRequest;
import com.app.LMS.DTO.SignupRequest;
import com.app.LMS.userManagement.controller.UserController;
import com.app.LMS.userManagement.service.AuthService;
import com.app.LMS.userManagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for testing
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    private SignupRequest signupRequest;
    private SignInRequest signInRequest;

    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest();
        signupRequest.setUsername("Alaa01");
        signupRequest.setPassword("password123");
        signupRequest.setEmail("test@example.com");
        signupRequest.setRole("INSTRUCTOR");
        signupRequest.setFirstName("Alaa");
        signupRequest.setLastName("Basuny");

        signInRequest = new SignInRequest();
        signInRequest.setUsername("testuser");
        signInRequest.setPassword("password123");
    }

    @Test
void testSignupSuccess() throws Exception {
    when(userService.registerUser(any(SignupRequest.class))).thenReturn("User registered successfully");

    mockMvc.perform(post("/api/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"testuser\", \"password\":\"password123\", \"email\":\"test@example.com\", \"role\":\"INSTRUCTOR\", \"firstName\":\"John\", \"lastName\":\"Doe\"}"))
            .andExpect(status().isCreated())
            .andExpect(content().string("User registered successfully"));
}

@Test
void testSignupFailure() throws Exception {
    when(userService.registerUser(any(SignupRequest.class))).thenReturn("Username already exists");

    mockMvc.perform(post("/api/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"testuser\", \"password\":\"password123\", \"email\":\"test@example.com\", \"role\":\"INSTRUCTOR\", \"firstName\":\"John\", \"lastName\":\"Doe\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Username already exists"));
}


    @Test
    void testSignInSuccess() throws Exception {
        when(authService.authenticate("testuser", "password123")).thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\", \"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("jwt-token"));
    }
    

    @Test
    void testSignupValidationFailure() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\", \"password\":\"password123\", \"email\":\"invalid-email\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSignInValidationFailure() throws Exception {
        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\", \"password\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}
