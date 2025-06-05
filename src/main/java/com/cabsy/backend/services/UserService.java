// src/main/java/com/cabsy/backend/services/UserService.java
package com.cabsy.backend.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.cabsy.backend.dtos.UserRegistrationDTO;
import com.cabsy.backend.dtos.UserResponseDTO;
import com.cabsy.backend.models.User;

public interface UserService {
    UserResponseDTO registerUser(UserRegistrationDTO registrationDTO);
    Optional<User> findUserByEmail(String email);
    Optional<UserResponseDTO> getUserById(Long id);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO updateUser(Long id, Map<String, String> updates); // This is already here
    // Add methods for updating profile, etc.
}