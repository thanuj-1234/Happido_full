// src/main/java/com/cabsy/backend/dtos/LoginDTO.java
package com.cabsy.backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data; // Lombok for boilerplate code (getters, setters, etc.)

/**
 * DTO for carrying login credentials (email and password) from the client.
 * Includes validation to ensure required fields are present and correctly formatted.
 */
@Data // Generates getters, setters, equals, hashCode, and toString methods
public class LoginDTO {

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long") // Assuming same min length as registration
    private String password; // Plaintext password from frontend for login
}
