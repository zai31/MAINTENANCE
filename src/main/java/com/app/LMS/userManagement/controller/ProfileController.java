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
    public ResponseEntity<ProfileDTO> getProfile(@RequestHeader("Authorization") String token) {
        Long id = jwtConfig.getUserIdFromToken(token);
        Optional<User> user = userService.findById(id);

        if (user.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        ProfileDTO profileResponse = new ProfileDTO(
                user.get().getFirstName(),
                user.get().getLastName(),
                user.get().getEmail()
        );

        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    // Update profile information
    @PatchMapping("/update")
    public ResponseEntity<String> updateProfile(@RequestHeader("Authorization") String token, @RequestBody ProfileDTO updateProfileRequest) {
        Long id = jwtConfig.getUserIdFromToken(token);
        Optional<User> user = userService.findById(id);

        if (user.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Update the profile information
        user.get().setFirstName(updateProfileRequest.getFirstName() != null ? updateProfileRequest.getFirstName() : user.get().getFirstName());
        user.get().setLastName(updateProfileRequest.getLastName() != null ? updateProfileRequest.getLastName() : user.get().getLastName());
        user.get().setEmail(updateProfileRequest.getEmail() != null ? updateProfileRequest.getEmail() : user.get().getEmail());

        userService.saveUser(user.get());

        return new ResponseEntity<>("Profile updated successfully", HttpStatus.OK);
    }
}
