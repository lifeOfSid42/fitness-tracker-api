package com.example.fitnesstracker.service;

import com.example.fitnesstracker.model.User;
import com.example.fitnesstracker.request.UserRequest;
import com.example.fitnesstracker.response.UserResponse;

import java.util.List;

public interface UserService {

    List<UserResponse> getAllUsersDto();

    UserResponse getUserByIdDto(Long id);

    UserResponse getUserByUsernameDto(String username);

    UserResponse createUserDto(UserRequest userRequest);

    UserResponse updateUserDto(Long id, UserRequest userRequest);

    void deleteUser(Long id);

    List<User> getAllUsers();

    User getUserById(Long id);

    User getUserByUsername(String username);

    User createUser(User user);
}
