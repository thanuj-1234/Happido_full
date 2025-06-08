// src/main/java/com/cabsy/backend/services/UserService.java
package com.cabsy.backend.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.cabsy.backend.dtos.UserRegistrationDTO;
import com.cabsy.backend.dtos.UserResponseDTO;
import com.cabsy.backend.dtos.UserPasswordResetConfirmationDTO; // NEW: Import for password reset
import com.cabsy.backend.models.User;

public interface UserService {
    UserResponseDTO registerUser(UserRegistrationDTO registrationDTO);
    Optional<User> findUserByEmail(String email); // Keep this if needed for internal service logic
    Optional<UserResponseDTO> getUserById(Long id);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO updateUser(Long id, Map<String, String> updates);

    /**
     * Resets the password for a user identified by their email.
     * This method handles finding the user, validating the new password (if required),
     * encoding it, and saving it.
     *
     * @param email The email of the user whose password needs to be reset.
     * @param newPassword The plain-text new password provided by the user.
     * @return A DTO confirming the password reset.
     * @throws RuntimeException if the user is not found or if the new password does not meet criteria.
     */
    UserPasswordResetConfirmationDTO resetUserPassword(String email, String newPassword); // MODIFIED
}