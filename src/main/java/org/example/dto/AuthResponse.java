package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor // ensures deserialization works (e.g., for JSON)
public class AuthResponse {
    private String token;
    private long expiresInMs;
}
