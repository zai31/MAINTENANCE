package com.app.LMS.DTO;
import jakarta.validation.constraints.NotBlank;

public class SignInRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public String getUsername()
    {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}
