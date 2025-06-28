package com.example.fitnesstracker.request;

import com.example.fitnesstracker.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record UserRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @Size(min = 6) String password,
        @Email String email,
        @NotBlank String fullName,
        User.Role role
) {
}