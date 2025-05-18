package com.app.LMS.userManagement.service;

import com.app.LMS.common.Exceptions.dedicatedException;
import com.app.LMS.userManagement.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.app.LMS.config.JwtConfig;

@Service
public class AuthService {

    private final UserService userService; // Assume UserService fetches user details from DB
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder, JwtConfig jwtConfig) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtConfig = jwtConfig;
    }

    public String authenticate(String username, String password) {
        User user = userService.findByUsername(username);


            if (user == null) {
                throw new dedicatedException.InvalidCredentialsException("Invalid username or password");
            }

            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new dedicatedException.InvalidCredentialsException("Invalid username or password");
            }

            return jwtConfig.generateToken(user.getId(), user.getRole().name());
        }
}
