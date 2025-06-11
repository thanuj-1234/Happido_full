// src/main/java/com/cabsy/backend/controllers/UserController.java
package com.cabsy.backend.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // NEW: Import for @RequestParam
import org.springframework.web.bind.annotation.RestController;

import com.cabsy.backend.dtos.ApiResponse;
import com.cabsy.backend.dtos.UserRegistrationDTO;
import com.cabsy.backend.dtos.UserResponseDTO;
import com.cabsy.backend.dtos.UserResetPasswordRequestDTO;
import com.cabsy.backend.dtos.UserPasswordResetConfirmationDTO;
import com.cabsy.backend.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(userDTO -> ResponseEntity.ok(ApiResponse.success("User fetched successfully", userDTO)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("User not found", "User with ID " + id + " does not exist")));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Users fetched successfully", users));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        try {
            UserResponseDTO registeredUser = userService.registerUser(registrationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("User registered successfully", registeredUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Registration failed", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An unexpected error occurred", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(@PathVariable Long id, @RequestBody Map<String, String> updates) {
        try {
            UserResponseDTO updatedUser = userService.updateUser(id, updates);
            return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("User not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Update failed", e.getMessage()));
            } else if (e.getMessage().contains("already taken")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error("Update failed", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Update failed", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An unexpected error occurred during update", e.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<UserPasswordResetConfirmationDTO>> forgotPassword(@Valid @RequestBody UserResetPasswordRequestDTO resetRequest) {
        try {
            UserPasswordResetConfirmationDTO confirmation = userService.resetUserPassword(resetRequest.getEmail(), resetRequest.getNewPassword());
            return ResponseEntity.ok(ApiResponse.success("Password reset request initiated. Check your email for further instructions (if tokens were implemented).", confirmation));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("User not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Password reset failed", e.getMessage()));
            } else if (e.getMessage().contains("Weak password") || e.getMessage().contains("New password does not meet complexity requirements")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Password reset failed", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An unexpected error occurred during password reset", e.getMessage()));
        }
    }

    // NEW: Endpoint to check if email exists
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailExists(@RequestParam String email) {
        boolean exists = userService.isEmailRegistered(email);
        if (exists) {
            return ResponseEntity.ok(ApiResponse.success("Email is already registered", true));
        } else {
            return ResponseEntity.ok(ApiResponse.success("Email is available", false));
        }
    }

    // TODO: Add DELETE mapping for deleting user (requires careful consideration)
}