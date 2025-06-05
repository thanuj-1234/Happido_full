// src/main/java/com/cabsy/backend/dtos/ApiResponse.java
package com.cabsy.backend.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String error; // For error messages

    // Convenience constructors
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static <T> ApiResponse<T> error(String message, String errorDetails) {
        return new ApiResponse<>(false, message, null, errorDetails);
    }
}