// src/main/java/com/cabsy/backend/services/impl/UserServiceImpl.java
package com.cabsy.backend.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cabsy.backend.dtos.UserRegistrationDTO;
import com.cabsy.backend.dtos.UserResponseDTO;
import com.cabsy.backend.dtos.UserPasswordResetConfirmationDTO;
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
            throw new RuntimeException("User with this email already exists");
        }
        if (userRepository.findByPhoneNumber(registrationDTO.getPhoneNumber()).isPresent()) {
            throw new RuntimeException("User with this phone number already exists");
        }

        User user = new User();
        user.setName(registrationDTO.getName());
        user.setEmail(registrationDTO.getEmail());
        user.setPhoneNumber(registrationDTO.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

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
    @Transactional
    public UserResponseDTO updateUser(Long id, Map<String, String> updates) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    user.setName(value);
                    break;
                case "email":
                    if (userRepository.findByEmail(value).isPresent() && !user.getEmail().equals(value)) {
                        throw new RuntimeException("Email already taken: " + value);
                    }
                    user.setEmail(value);
                    break;
                case "phoneNumber":
                    if (userRepository.findByPhoneNumber(value).isPresent() && !user.getPhoneNumber().equals(value)) {
                        throw new RuntimeException("Phone number already taken: " + value);
                    }
                    user.setPhoneNumber(value);
                    break;
                case "password":
                    user.setPassword(passwordEncoder.encode(value));
                    break;
                default:
                    System.out.println("Ignoring unknown or unsupported field for update: " + key);
                    break;
            }
        });

        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        return new UserResponseDTO(updatedUser.getId(), updatedUser.getName(), updatedUser.getEmail(), updatedUser.getPhoneNumber(), updatedUser.getRating());
    }

    @Override
    @Transactional
    public UserPasswordResetConfirmationDTO resetUserPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$";
        if (!Pattern.matches(passwordRegex, newPassword)) {
            throw new RuntimeException("New password does not meet complexity requirements. It must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return new UserPasswordResetConfirmationDTO("Password for user with email " + email + " has been successfully reset.", email);
    }

    @Override
    public boolean isEmailRegistered(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}