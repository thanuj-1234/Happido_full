// src/main/java/com/cabsy/backend/services/UserService.java
package com.cabsy.backend.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.cabsy.backend.dtos.UserRegistrationDTO;
import com.cabsy.backend.dtos.UserResponseDTO;
import com.cabsy.backend.dtos.UserPasswordResetConfirmationDTO;
import com.cabsy.backend.models.User;

public interface UserService {
    UserResponseDTO registerUser(UserRegistrationDTO registrationDTO);
    Optional<User> findUserByEmail(String email);
    Optional<UserResponseDTO> getUserById(Long id);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO updateUser(Long id, Map<String, String> updates);
    UserPasswordResetConfirmationDTO resetUserPassword(String email, String newPassword);

    /**
     * Checks if a user with the given email address is already registered.
     *
     * @param email The email address to check.
     * @return true if an account with this email exists, false otherwise.
     */
    boolean isEmailRegistered(String email); // NEW: Method to check email existence
}