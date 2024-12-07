package com.app.LMS.userManagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.app.LMS.userManagement.service.UserService;
import com.app.LMS.userManagement.service.AuthService;
import jakarta.validation.Valid;
import com.app.LMS.DTO.SignupRequest;
import com.app.LMS.DTO.SignInRequest;

@RestController
@RequestMapping("/api/auth")
public class UserController {


    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest userInfo) {
        String result = userService.registerUser(userInfo);
        if ("User registered successfully".equals(result))
        {
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        }
        else
        {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signIn(@RequestBody @Valid SignInRequest signInRequest) {
        String token = authService.authenticate(signInRequest.getUsername(), signInRequest.getPassword());
        return ResponseEntity.ok(token);
    }

}
