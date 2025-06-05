// src/main/java/com/cabsy/backend/services/impl/UserServiceImpl.java
package com.cabsy.backend.services.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Use Spring's @Transactional

import com.cabsy.backend.dtos.UserRegistrationDTO;
import com.cabsy.backend.dtos.UserResponseDTO;
import com.cabsy.backend.models.User;
import com.cabsy.backend.repositories.UserRepository;
import com.cabsy.backend.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponseDTO registerUser(UserRegistrationDTO registrationDTO) {
        // Check if user with email or phone already exists
        if (userRepository.findByEmail(registrationDTO.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists"); // TODO: Custom exception
        }
        if (userRepository.findByPhoneNumber(registrationDTO.getPhoneNumber()).isPresent()) {
            throw new RuntimeException("User with this phone number already exists"); // TODO: Custom exception
        }

        User user = new User();
        user.setName(registrationDTO.getName());
        user.setEmail(registrationDTO.getEmail());
        user.setPhoneNumber(registrationDTO.getPhoneNumber());
        // HASH THE PASSWORD BEFORE SAVING
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));

        User savedUser = userRepository.save(user);
        return new UserResponseDTO(savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getPhoneNumber(), savedUser.getRating());
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<UserResponseDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getPhoneNumber(), user.getRating()));
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getPhoneNumber(), user.getRating()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional // Ensure transactional for update operations
    public UserResponseDTO updateUser(Long id, Map<String, String> updates) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id)); // TODO: Custom exception

        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    user.setName(value);
                    break;
                case "email":
                    // Optionally add email uniqueness check here
                    if (userRepository.findByEmail(value).isPresent() && !user.getEmail().equals(value)) {
                        throw new RuntimeException("Email already taken: " + value); // TODO: Custom exception
                    }
                    user.setEmail(value);
                    break;
                case "phoneNumber":
                    // Optionally add phone number uniqueness check here
                    if (userRepository.findByPhoneNumber(value).isPresent() && !user.getPhoneNumber().equals(value)) {
                        throw new RuntimeException("Phone number already taken: " + value); // TODO: Custom exception
                    }
                    user.setPhoneNumber(value);
                    break;
                case "password":
                    // Always hash the new password
                    user.setPassword(passwordEncoder.encode(value));
                    break;
                // Add more fields as needed, e.g., "address", "rating" (if updatable)
                default:
                    // Optionally throw an exception for unsupported fields or ignore them
                    System.out.println("Ignoring unknown field: " + key);
                    break;
            }
        });

        User updatedUser = userRepository.save(user);
        return new UserResponseDTO(updatedUser.getId(), updatedUser.getName(), updatedUser.getEmail(), updatedUser.getPhoneNumber(), updatedUser.getRating());
    }
}