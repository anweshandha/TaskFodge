package org.example.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class AuthRequest {
    @NotBlank private String username; // username or email
    @NotBlank private String password;
}
