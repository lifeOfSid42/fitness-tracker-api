package com.example.fitnesstracker.mapper;

import com.example.fitnesstracker.model.User;
import com.example.fitnesstracker.request.UserRequest;
import com.example.fitnesstracker.response.UserResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between User entity and its DTO representations.
 */
@Component
public class UserMapper {

    /**
     * Converts a UserRequest DTO to a User entity.
     *
     * @param userRequest the UserRequest DTO
     * @return the User entity
     */
    public User toEntity(UserRequest userRequest) {
        if (userRequest == null) {
            return null;
        }

        User user = new User();
        user.setUsername(userRequest.username());
        user.setPassword(userRequest.password());
        user.setEmail(userRequest.email());
        user.setFullName(userRequest.fullName());

        if (userRequest.role() != null) {
            user.setRole(userRequest.role());
        }

        return user;
    }

    /**
     * Converts a User entity to a UserResponse DTO.
     *
     * @param user the User entity
     * @return the UserResponse DTO
     */
    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    /**
     * Updates an existing User entity with data from a UserRequest DTO.
     *
     * @param user        the existing User entity
     * @param userRequest the UserRequest DTO with updated data
     * @return the updated User entity
     */
    public User updateEntityFromRequest(User user, UserRequest userRequest) {
        if (user == null || userRequest == null) {
            return user;
        }

        user.setUsername(userRequest.username());
        user.setEmail(userRequest.email());
        user.setFullName(userRequest.fullName());

        if (userRequest.password() != null && !userRequest.password().isEmpty()) {
            user.setPassword(userRequest.password());
        }

        if (userRequest.role() != null) {
            user.setRole(userRequest.role());
        }

        return user;
    }
}