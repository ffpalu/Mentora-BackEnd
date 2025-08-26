package com.backend.mentora.dto.response;

import com.backend.mentora.entity.enums.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private UserRole role;
    private LocalDateTime createdAt;
    private Boolean isActive;
}
