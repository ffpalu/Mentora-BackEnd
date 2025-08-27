package com.backend.mentora.dto.request;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class QuestionnaireRequest {

    @NotNull
    private List<String> mainReasons;

    private List<Map<String, Object>> emotionalStates;

    @Min(0) @Max(120)
    private Integer emotionalDurationMonths;

    private List<String> associatedBehaviors;

    @Min(0) @Max(120)
    private Integer behaviorsDurationMonths;

    @Min(0) @Max(120)
    private Integer griefTimeLineMonths;

    private Boolean hasSocialSupport;
    private Boolean showDepressiveSymptoms;


    @Min(0) @Max(120)
    private Integer depressiveSymptomsMonths;

    @Min(1) @Max(4)
    private Integer impactLevel;


    private String addictionType;
    private Boolean isInRehabilitation;
    private String violentBehavioursFrequency;



}
