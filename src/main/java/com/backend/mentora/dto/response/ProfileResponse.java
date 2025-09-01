package com.backend.mentora.dto.response;

import com.backend.mentora.entity.enums.PsychologistSpecialization;
import com.backend.mentora.entity.enums.SessionMode;
import com.backend.mentora.entity.enums.UserRole;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class ProfileResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private UserRole role;
    private LocalDateTime createdAt;
    private Boolean isActive;

    private Integer age;
    private SessionMode preferredSessionMode;
    private String city;
    private String region;
    private Boolean hasCompletedQuestionnaire;

    private String licenseNumber;
    private String biography;
    private Integer yearsExperience;
    private BigDecimal hourlyRate;
    private Boolean offersOnlineSessions;
    private Boolean offersInPersonSessions;
    private Set<PsychologistSpecialization> specializations;
    private Set<String> operatingCities;

}
