package com.backend.mentora.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private UserResponse user;
}
