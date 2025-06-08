// src/main/java/com/cabsy/backend/dtos/DriverRegistrationDTO.java
package com.cabsy.backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data; // Lombok for boilerplate code (getters, setters, etc.)

/**
 * DTO for carrying driver registration data from the client to the server.
 * Includes validation annotations to ensure data integrity before processing.
 */
@Data // Generates getters, setters, equals, hashCode, and toString methods
public class DriverRegistrationDTO {

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String name;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @NotBlank(message = "Phone number cannot be empty")
    @Pattern(regexp = "^\\+?[0-9.()-]{7,20}$", message = "Phone number is invalid. It should be 7-20 digits and can include +, ., (, ).")
    private String phoneNumber;

    @NotBlank(message = "License number cannot be empty")
    @Size(min = 5, max = 50, message = "License number must be between 5 and 50 characters")
    private String licenseNumber;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters long and not exceed 100 characters")
    private String password; // Plaintext password from frontend (will be hashed in service)
}
