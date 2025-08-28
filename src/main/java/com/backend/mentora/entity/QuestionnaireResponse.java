package com.backend.mentora.entity;

import com.backend.mentora.entity.enums.Priority;
import com.backend.mentora.entity.enums.PsychologistSpecialization;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "questionnaire_responses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "main_reasons", columnDefinition = "TEXT")
    private String mainReasons;

    @Column(name = "emotional_states", columnDefinition = "TEXT")
    private String emotionalStates;

    @Column(name = "emotional_duration_months")
    private Integer emotionalDurationMonths;

    @Column(name = "associated_behaviors", columnDefinition = "TEXT")
    private String associatedBehaviors;

    @Column(name = "behaviors_duration_months")
    private Integer behaviorsDurationMonths;

    @Enumerated(EnumType.STRING)
    @Column(name = "calculated_priority")
    private Priority calculatedPriority;

    @Enumerated(EnumType.STRING)
    @Column(name = "required_specialization", nullable = false)
    private PsychologistSpecialization requiredSpecialization;

    @Column(name = "grief_timeline_months")
    private Integer griefTimelineMonths;

    @Column(name = "has_social_support")
    private Boolean hasSocialSupport;

    @Column(name = "shows_depressive_symptoms")
    private Boolean showsDepressiveSymptoms;

    @Column(name = "depressive_symptoms_duration_months")
    private Integer depressiveSymptomsMonths;

    @Column(name = "impact_level")
    private Integer impactLevel;

    @Column(name = "addiction_type")
    private String addictionType;

    @Column(name = "is_in_rehabilitation")
    private Boolean isInRehabilitation;

    @Column(name = "violent_behaviors_frequency")
    private String violentBehaviorsFrequency;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;


    @PrePersist
    protected void onCreate() {
        completedAt = LocalDateTime.now();
        calculatePriority();
        calculateRequiredSpecializations();

    }

    private void calculateRequiredSpecializations() {
        if(client != null && client.getAge() != null)
            this.requiredSpecialization = PsychologistSpecialization.getByAge(client.getAge());
        else
            this.requiredSpecialization = PsychologistSpecialization.ADULT_PSYCHOLOGY;
    }

    private void calculatePriority() {
        this.calculatedPriority = Priority.NORMAL;

        if (isHighPriority())
            this.calculatedPriority = Priority.HIGH;
        else  if (isModeratePriority())
            this.calculatedPriority = Priority.MODERATE;

    }

    private boolean isHighPriority() {
        return (mainReasons!= null &&
                (mainReasons.contains("Dipendenze") || mainReasons.contains("Esperienze traumatiche recenti"))) ||
                (violentBehaviorsFrequency != null && violentBehaviorsFrequency.contains("Spesso"));
    }

    private boolean isModeratePriority() {
        return (emotionalDurationMonths != null && emotionalDurationMonths >= 1) ||
                (showsDepressiveSymptoms != null && showsDepressiveSymptoms &&
                        depressiveSymptomsMonths!= null && depressiveSymptomsMonths >= 1) ||
                (impactLevel != null && impactLevel >= 3);
    }

    public List<String> getMainReasonsAsList() {
        return parseJsonArray(mainReasons);
    }

    public List<Map<String, Object>> getEmotionalStatesAsList() {
        return parseJsonObjectArray(emotionalStates);
    }

    public List<String> getAssociatedBehaviorsAsList() {
        return parseJsonArray(associatedBehaviors);
    }


    private List<String> parseJsonArray(String json) {
        if(json == null || json.trim().isEmpty()) return List.of();
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, String.class));
        }
        catch (JsonProcessingException e) {
            return List.of();
        }
    }

    private List<Map<String, Object>> parseJsonObjectArray(String json) {
        if(json == null || json.trim().isEmpty()) return List.of();
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, Map.class));
        }
        catch (JsonProcessingException e) {
            return List.of();
        }
    }

}
