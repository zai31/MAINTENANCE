package com.app.LMS.userManagement.controller;

import com.app.LMS.DTO.ProfileDTO;
import com.app.LMS.config.JwtConfig;
import com.app.LMS.userManagement.model.User;
import com.app.LMS.userManagement.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;
    private final JwtConfig jwtConfig;

    public ProfileController(UserService userService, JwtConfig jwtConfig) {
        this.userService = userService;
        this.jwtConfig = jwtConfig;
    }

    // Get profile information
    @GetMapping("/view")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        Long id = jwtConfig.getUserIdFromToken(token);

        Optional<User> user = userService.findById(id);
        if (user.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        ProfileDTO profileResponse = new ProfileDTO(user.get().getFirstName(), user.get().getLastName(), user.get().getEmail());
        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    // Update profile information
    @PatchMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String token, @RequestBody ProfileDTO updateProfileRequest) {

        Long id = jwtConfig.getUserIdFromToken(token);

        Optional<User> user = userService.findById(id);
        if (user.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Update the profile information
        if (updateProfileRequest.getFirstName() != null) {
            user.get().setFirstName(updateProfileRequest.getFirstName());
        }
        if (updateProfileRequest.getLastName() != null) {
            user.get().setLastName(updateProfileRequest.getLastName());
        }
        if (updateProfileRequest.getEmail() != null) {
            user.get().setEmail(updateProfileRequest.getEmail());
        }

        userService.saveUser(user.get());

        return new ResponseEntity<>("Profile updated successfully", HttpStatus.OK);
    }
}
