package com.app.LMS.userManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull(message = "Username cannot be null")
    @Column(nullable = false, unique = true)
    private String username;

    @NotNull(message = "First name cannot be null")
    @Column(nullable = false)
    private String firstName;

    @NotNull(message = "Last name cannot be null")
    @Column(nullable = false)
    private String lastName;

    @NotNull(message = "Password cannot be null")
    @Column(nullable = false)
    private String password;

    @NotNull(message = "Email cannot be null")
    @Column(nullable = false, unique = true)
    @Email
    private String email;

    @NotNull(message = "Role cannot be null")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role; // Role should be an enum
    public enum Role {
        ADMIN,
        INSTRUCTOR,
        STUDENT
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

}
