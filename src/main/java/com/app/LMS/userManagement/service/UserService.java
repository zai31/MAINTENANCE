package com.app.LMS.userManagement.service;

import com.app.LMS.userManagement.model.User;
import com.app.LMS.userManagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.app.LMS.DTO.SignupRequest;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String registerUser(SignupRequest signupRequest) {
        // Check if the username or email already exists
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return "Username already exists";
        }
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return "Email already exists";
        }

        // Create and save the user with the encoded password
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword())); // Encode the password
        user.setRole(User.Role.valueOf(signupRequest.getRole())); // Set the role

        userRepository.save(user);
        return "User registered successfully";
    }

    public User findByUsername(String username)
    {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
}
