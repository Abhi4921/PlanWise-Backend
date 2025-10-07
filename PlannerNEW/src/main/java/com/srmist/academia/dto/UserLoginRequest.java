package com.srmist.academia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UserLoginRequest {

    @NotBlank
    @Email(message = "Invalid email format")
    @Pattern(regexp = ".*@srmist\\.edu\\.in$", message = "Email must end with @srmist.edu.in")
    private String email;

    @NotBlank
    private String password;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
