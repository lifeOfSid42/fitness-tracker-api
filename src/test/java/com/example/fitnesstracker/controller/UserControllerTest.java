package com.example.fitnesstracker.controller;

import com.example.fitnesstracker.model.User;
import com.example.fitnesstracker.request.UserRequest;
import com.example.fitnesstracker.response.UserResponse;
import com.example.fitnesstracker.security.UserSecurity;
import com.example.fitnesstracker.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean(name = "userSecurity")
    private UserSecurity userSecurity;

    private UserResponse userResponse;
    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        userResponse = new UserResponse(
                1L, "testUser", "test@example.com", "Test User",
                User.Role.USER, LocalDateTime.now()
        );

        userRequest = new UserRequest(
                "testUser", "password", "test@example.com", "Test User", User.Role.USER
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_shouldReturnUserList() throws Exception {
        when(userService.getAllUsersDto()).thenReturn(List.of(userResponse));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testUser"));

        verify(userService).getAllUsersDto();
    }

    @Test
    @WithMockUser(username = "testUser")
    void getUserById_shouldReturnUser_whenUserAuthorized() throws Exception {
        when(userSecurity.isCurrentUser(1L)).thenReturn(true);
        when(userService.getUserByIdDto(1L)).thenReturn(userResponse);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"));

        verify(userService).getUserByIdDto(1L);
    }

    @Test
    @WithMockUser
    void createUser_shouldReturnCreatedUser() throws Exception {
        when(userService.createUserDto(any(UserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testUser"));

        verify(userService).createUserDto(any(UserRequest.class));
    }

    @Test
    @WithMockUser(username = "testUser")
    void updateUser_shouldReturnUpdatedUser_whenUserAuthorized() throws Exception {
        when(userSecurity.isCurrentUser(1L)).thenReturn(true);
        when(userService.updateUserDto(eq(1L), any(UserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(put("/api/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"));

        verify(userService).updateUserDto(eq(1L), any(UserRequest.class));
    }

    @Test
    @WithMockUser(username = "testUser")
    void deleteUser_shouldReturnNoContent_whenAuthorized() throws Exception {
        when(userSecurity.isCurrentUser(1L)).thenReturn(true);
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }
}