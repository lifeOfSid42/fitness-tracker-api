package com.example.fitnesstracker.service;

import com.example.fitnesstracker.exception.BadRequestException;
import com.example.fitnesstracker.exception.ResourceNotFoundException;
import com.example.fitnesstracker.mapper.UserMapper;
import com.example.fitnesstracker.model.User;
import com.example.fitnesstracker.repository.UserRepository;
import com.example.fitnesstracker.request.UserRequest;
import com.example.fitnesstracker.response.UserResponse;
import com.example.fitnesstracker.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRequest userRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setRole(User.Role.USER);
        user.setCreatedAt(LocalDateTime.now());

        userRequest = new UserRequest(
                "testUser",
                "password",
                "test@example.com",
                "Test User",
                User.Role.USER
        );

        userResponse = new UserResponse(
                1L,
                "testUser",
                "test@example.com",
                "Test User",
                User.Role.USER,
                user.getCreatedAt()
        );
    }

    @Test
    void getAllUsersDto_ShouldReturnUserResponses() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        List<UserResponse> result = userService.getAllUsersDto();

        assertEquals(1, result.size());
        assertEquals("testUser", result.get(0).username());
    }

    @Test
    void getUserByIdDto_ShouldReturnUserResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);
        UserResponse result = userService.getUserByIdDto(1L);
        assertEquals("testUser", result.username());
    }

    @Test
    void getUserByUsernameDto_ShouldReturnUserResponse() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);
        UserResponse result = userService.getUserByUsernameDto("testUser");
        assertEquals("testUser", result.username());
    }

    @Test
    void createUserDto_ShouldCreateUser() {
        User toBeSaved = new User();
        toBeSaved.setUsername(userRequest.username());
        toBeSaved.setEmail(userRequest.email());
        toBeSaved.setPassword(userRequest.password()); // raw password here

        when(userMapper.toEntity(userRequest)).thenReturn(toBeSaved);
        when(userRepository.existsByUsername("testUser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testUser");
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("encodedPassword");
        savedUser.setFullName("Test User");
        savedUser.setRole(User.Role.USER);
        savedUser.setCreatedAt(LocalDateTime.now());

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(userResponse);

        UserResponse result = userService.createUserDto(userRequest);

        assertEquals("testUser", result.username());
    }

    @Test
    void updateUserDto_ShouldUpdateUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.updateEntityFromRequest(user, userRequest)).thenReturn(user);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.updateUserDto(1L, userRequest);

        assertEquals("testUser", result.username());
        assertEquals("test@example.com", result.email());
    }

    @Test
    void deleteUser_ShouldDeleteUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.deleteUser(1L);
        verify(userRepository).delete(user);
    }

    @Test
    void getUserById_NotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void createUser_WhenUsernameExists_ShouldThrowException() {
        when(userMapper.toEntity(userRequest)).thenReturn(user);
        when(userRepository.existsByUsername("testUser")).thenReturn(true);
        assertThrows(BadRequestException.class, () -> userService.createUserDto(userRequest));
    }

    @Test
    void createUser_WhenEmailExists_ShouldThrowException() {
        when(userMapper.toEntity(userRequest)).thenReturn(user);
        when(userRepository.existsByUsername("testUser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        assertThrows(BadRequestException.class, () -> userService.createUserDto(userRequest));
    }
}
