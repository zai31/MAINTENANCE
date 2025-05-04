package com.app.LMS.DTO;

import jakarta.validation.constraints.NotBlank;

public class SignInRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    // Getters
    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
