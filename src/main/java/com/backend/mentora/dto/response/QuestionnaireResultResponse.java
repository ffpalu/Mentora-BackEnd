package com.backend.mentora.dto.response;


import com.backend.mentora.entity.enums.Priority;
import com.backend.mentora.entity.enums.PsychologistSpecialization;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class QuestionnaireResultResponse {

    private Long id;
    private Priority calculatedPriority;
    private PsychologistSpecialization requiredSpecialization;
    private LocalDateTime completedAt;

    private String priorityMessage;
    private String specializationMessage;
    private String nextStepsMessage;


    private List<PsychologistRecommendationResponse> recommendedPsychologists;

}
