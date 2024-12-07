package com.app.LMS.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class SignupRequest {
    @NotNull @NotEmpty
    private String username;

    @NotNull @NotEmpty @Email
    private String email;

    @NotNull
    @NotEmpty
    private String password;

    private String role;
    private String firstName;
    private String lastName;

    public String getUsername()
    {
        return this.username;
    }

    public String getEmail()
    {
        return this.email;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName()
    {
        return this.lastName;
    }

    public String getPassword()
    {
        return this.password;
    }

    public String getRole()
    {
        return this.role;
    }

}
