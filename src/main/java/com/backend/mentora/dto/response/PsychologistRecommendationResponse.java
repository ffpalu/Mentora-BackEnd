package com.backend.mentora.dto.response;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PsychologistRecommendationResponse {
    private Long id;
    private String fullName;
    private String biography;
    private Integer yearsExperience;
    private List<String> specialization;
    private Boolean offersOnlineSessions;
    private List<String> operatingCities;
    private String matchReason;
}
