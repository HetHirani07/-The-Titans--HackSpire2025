package com.MindMosaic.MindMosaic.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token; // JWT token for authentication
    private String email; // User's email address,returns authentication response data.
}
