// src/main/java/com/cabsy/backend/dtos/UserPasswordResetConfirmationDTO.java
package com.cabsy.backend.dtos;

public class UserPasswordResetConfirmationDTO {
    private String message;
    private String email; // Optionally include the email for confirmation

    public UserPasswordResetConfirmationDTO(String message, String email) {
        this.message = message;
        this.email = email;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}