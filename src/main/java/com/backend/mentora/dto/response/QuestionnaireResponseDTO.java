package com.backend.mentora.dto.response;

import com.backend.mentora.entity.Client;
import com.backend.mentora.entity.enums.Priority;
import com.backend.mentora.entity.enums.PsychologistSpecialization;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class QuestionnaireResponseDTO {

    private long id;

    private String client;

    private String mainReasons;

    private String emotionalStates;

    private Integer emotionalDurationMonths;

    private String associatedBehaviors;

    private Integer behaviorsDurationMonths;

    private Priority calculatedPriority;

    private PsychologistSpecialization requiredSpecialization;

    private Integer griefTimelineMonths;

    private Boolean hasSocialSupport;

    private Boolean showsDepressiveSymptoms;

    private Integer depressiveSymptomsMonths;

    private Integer impactLevel;

    private String addictionType;

    private Boolean isInRehabilitation;

    private String violentBehaviorsFrequency;

    private LocalDateTime completedAt;
}
