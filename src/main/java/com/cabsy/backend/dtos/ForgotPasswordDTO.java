// src/main/java/com/cabsy/backend/dtos/ForgotPasswordDTO.java
package com.cabsy.backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for carrying data related to a forgot password request,
 * containing the user's email and the new password.
 */
public class ForgotPasswordDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String newPassword;

    /**
     * Get the email address.
     * @return The email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the email address.
     * @param email The email address to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the new password.
     * @return The new password.
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * Set the new password.
     * @param newPassword The new password to set.
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
