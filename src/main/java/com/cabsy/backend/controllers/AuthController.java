// src/main/java/com/cabsy/backend/controllers/AuthController.java
package com.cabsy.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cabsy.backend.dtos.ApiResponse;
import com.cabsy.backend.dtos.DriverRegistrationDTO;
import com.cabsy.backend.dtos.DriverResponseDTO;
import com.cabsy.backend.dtos.LoginDTO;
import com.cabsy.backend.dtos.UserRegistrationDTO;
import com.cabsy.backend.dtos.UserResponseDTO;
import com.cabsy.backend.services.DriverService; // For DTO validation
import com.cabsy.backend.services.UserService; // Needed for login check

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final DriverService driverService;
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder for login

    public AuthController(UserService userService, DriverService driverService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.driverService = driverService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/user/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        try {
            UserResponseDTO newUser = userService.registerUser(registrationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("User registered successfully", newUser));
        } catch (RuntimeException e) { // Catch specific exceptions for better error messages
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("User registration failed", e.getMessage()));
        }
        
    }
   
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    
    @PostMapping("/driver/register")
    public ResponseEntity<ApiResponse<DriverResponseDTO>> registerDriver(@Valid @RequestBody DriverRegistrationDTO registrationDTO) {
        try {
            DriverResponseDTO newDriver = driverService.registerDriver(registrationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Driver registered successfully", newDriver));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Driver registration failed", e.getMessage()));
        }
    }

    @PostMapping("/user/login")
    public ResponseEntity<ApiResponse<String>> loginUser(@Valid @RequestBody LoginDTO loginDTO) {
        // This is a basic login check. For real-world, you'd generate a JWT token here.
        return (ResponseEntity<ApiResponse<String>>) userService.findUserByEmail(loginDTO.getEmail())
            .map(user -> {
                if (passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
                    UserResponseDTO userResponse = new UserResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        user.getRating()
                        // If you had a JWT token, you'd add it to UserResponseDTO and return it here
                        // user.getJwtToken()
                    );
                    return ResponseEntity.ok(ApiResponse.success("User logged in successfully", userResponse));
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Login failed", "Invalid credentials"));
                }
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Login failed", "Invalid credentials")));
    }

    @PostMapping("/driver/login")
    public ResponseEntity<ApiResponse<String>> loginDriver(@Valid @RequestBody LoginDTO loginDTO) {
        // This is a basic login check. For real-world, you'd generate a JWT token here.
        return (ResponseEntity<ApiResponse<String>>) driverService.findDriverByEmail(loginDTO.getEmail())
            .map(driver -> {
                if (passwordEncoder.matches(loginDTO.getPassword(), driver.getPassword())) {
                    // TODO: Implement JWT token generation and return it
                    DriverResponseDTO driverResponse = new DriverResponseDTO(
                        driver.getId(),
                        driver.getName(),
                        driver.getEmail(),
                        driver.getPhoneNumber(),
                        driver.getLicenseNumber(),
                        driver.getStatus(),
                        driver.getRating()
                        // If you had a JWT token, you'd add it to DriverResponseDTO and return it here
                        // driver.getJwtToken()
                    );
                    return ResponseEntity.ok(ApiResponse.success("Driver logged in successfully",driverResponse));
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Login failed", "Invalid credentials"));
                }
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Login failed", "Invalid credentials")));
    }
}