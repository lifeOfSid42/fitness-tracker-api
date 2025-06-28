package com.example.fitnesstracker.response;

import com.example.fitnesstracker.model.User;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String email,
        String fullName,
        User.Role role,
        LocalDateTime createdAt
) {
}