package com.backend.mentora.controller;


import com.backend.mentora.dto.request.QuestionnaireRequest;
import com.backend.mentora.dto.response.QuestionnaireResponseDTO;
import com.backend.mentora.dto.response.QuestionnaireResultResponse;
import com.backend.mentora.entity.QuestionnaireResponse;
import com.backend.mentora.service.QuestionnaireService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questionnaire")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENT')")
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;

    @PostMapping
    public ResponseEntity<QuestionnaireResultResponse> submitQuestionnaire(
            @Valid @RequestBody QuestionnaireRequest request,
            Authentication auth
            ){

        QuestionnaireResultResponse response = questionnaireService.processQuestionnaire(auth.getName(), request);

        return ResponseEntity.ok(response);

    }

    @GetMapping
    public ResponseEntity<QuestionnaireResponseDTO> getQuestionnaire(Authentication auth) {
        QuestionnaireResponse questionnaire = questionnaireService.getClientQuestionnaire(auth.getName());

        QuestionnaireResponseDTO response = QuestionnaireResponseDTO.builder()
                .id(questionnaire.getId())
                .client(questionnaire.getClient().getFullName())
                .mainReasons(questionnaire.getMainReasons())
                .mainReasonDuration(questionnaire.getMainReasonDuration())
								.calculatedPriority(questionnaire.getCalculatedPriority())
								.requiredSpecialization(questionnaire.getRequiredSpecialization())
                .emotionalStates(questionnaire.getEmotionalStates())
                .emotionalDurationMonths(questionnaire.getEmotionalDurationMonths())
                .associatedBehaviors(questionnaire.getAssociatedBehaviors())
                .behaviorsDurationMonths(questionnaire.getBehaviorsDurationMonths())
                .griefTimelineMonths(questionnaire.getGriefTimelineMonths())
                .hasSocialSupport(questionnaire.getHasSocialSupport())
                .showsDepressiveSymptoms(questionnaire.getShowsDepressiveSymptoms())
                .depressiveSymptomsMonths(questionnaire.getDepressiveSymptomsMonths())
                .impactLevel(questionnaire.getImpactLevel())
                .addictionType(questionnaire.getAddictionType())
                .isInRehabilitation(questionnaire.getIsInRehabilitation())
                .violentBehaviorsFrequency(questionnaire.getViolentBehaviorsFrequency())
                .completedAt(questionnaire.getCompletedAt())
                .build();

        return ResponseEntity.ok(response);
    }



}
