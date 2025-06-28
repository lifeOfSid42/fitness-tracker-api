package com.example.fitnesstracker.service.impl;

import com.example.fitnesstracker.exception.BadRequestException;
import com.example.fitnesstracker.exception.ResourceNotFoundException;
import com.example.fitnesstracker.mapper.UserMapper;
import com.example.fitnesstracker.model.User;
import com.example.fitnesstracker.repository.UserRepository;
import com.example.fitnesstracker.request.UserRequest;
import com.example.fitnesstracker.response.UserResponse;
import com.example.fitnesstracker.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserResponse> getAllUsersDto() {
        log.info("Fetching all users");
        return getAllUsers().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserByIdDto(Long id) {
        log.info("Fetching user DTO by ID: {}", id);
        return userMapper.toResponse(getUserById(id));
    }

    @Override
    public UserResponse getUserByUsernameDto(String username) {
        log.info("Fetching user DTO by username: {}", username);
        return userMapper.toResponse(getUserByUsername(username));
    }

    @Override
    public UserResponse createUserDto(UserRequest userRequest) {
        log.info("Creating user DTO with username: {}", userRequest.username());
        User saved = createUser(userMapper.toEntity(userRequest));
        return userMapper.toResponse(saved);
    }

    @Override
    public UserResponse updateUserDto(Long id, UserRequest userRequest) {
        log.info("Updating user DTO with ID: {}", id);
        User user = getUserById(id);

        if (!user.getUsername().equals(userRequest.username()) &&
                userRepository.existsByUsername(userRequest.username())) {
            log.warn("Username '{}' is already taken", userRequest.username());
            throw new BadRequestException("Username is already taken");
        }

        if (!user.getEmail().equals(userRequest.email()) &&
                userRepository.existsByEmail(userRequest.email())) {
            log.warn("Email '{}' is already in use", userRequest.email());
            throw new BadRequestException("Email is already in use");
        }

        user = userMapper.updateEntityFromRequest(user, userRequest);

        if (userRequest.password() != null && !userRequest.password().isEmpty()) {
            log.debug("Encoding new password for user ID: {}", id);
            user.setPassword(passwordEncoder.encode(userRequest.password()));
        }

        User saved = userRepository.save(user);
        log.debug("User updated: {}", saved.getId());
        return userMapper.toResponse(saved);
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("Retrieving all user entities");
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        log.debug("Retrieving user by ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User", "id", id);
                });
    }

    @Override
    public User getUserByUsername(String username) {
        log.debug("Retrieving user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found with username: {}", username);
                    return new ResourceNotFoundException("User", "username", username);
                });
    }

    @Override
    public User createUser(User user) {
        log.info("Creating user with username: {}", user.getUsername());

        if (userRepository.existsByUsername(user.getUsername())) {
            log.warn("Username '{}' is already taken", user.getUsername());
            throw new BadRequestException("Username is already taken");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("Email '{}' is already in use", user.getEmail());
            throw new BadRequestException("Email is already in use");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        log.debug("User created with ID: {}", saved.getId());
        return saved;
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        userRepository.delete(getUserById(id));
        log.debug("User deleted with ID: {}", id);
    }
}