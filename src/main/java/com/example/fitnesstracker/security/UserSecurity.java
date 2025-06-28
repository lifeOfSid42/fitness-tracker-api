package com.example.fitnesstracker.security;

import com.example.fitnesstracker.model.User;
import com.example.fitnesstracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserSecurity {

    private final UserService userService;

    @Autowired
    public UserSecurity(UserService userService) {
        this.userService = userService;
    }

    public boolean isCurrentUser(Long userId) {
        String currentUsername = SecurityUtils.getCurrentUsername();
        if (currentUsername == null) {
            return false;
        }

        try {
            User user = userService.getUserById(userId);
            return user.getUsername().equals(currentUsername);
        } catch (Exception e) {
            return false;
        }
    }
}