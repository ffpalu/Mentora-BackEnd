package com.backend.mentora.service;

import com.backend.mentora.dto.request.QuestionnaireRequest;
import com.backend.mentora.dto.response.PsychologistRecommendationResponse;
import com.backend.mentora.dto.response.QuestionnaireResultResponse;
import com.backend.mentora.entity.Client;
import com.backend.mentora.entity.Location;
import com.backend.mentora.entity.Psychologist;
import com.backend.mentora.entity.QuestionnaireResponse;
import com.backend.mentora.entity.enums.Priority;
import com.backend.mentora.entity.enums.PsychologistSpecialization;
import com.backend.mentora.exception.ValidationException;
import com.backend.mentora.repository.ClientRepository;
import com.backend.mentora.repository.PsychologistRepository;
import com.backend.mentora.repository.QuestionnaireResponseRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionnaireService {

    private final ClientRepository clientRepository;
    private final QuestionnaireResponseRepository questionnaireRepository;
    private final PsychologistRepository psychologistRepository;
    private final MatchingService matchingService;
    private final ObjectMapper objectMapper;


    public QuestionnaireResultResponse processQuestionnaire(String clientEmail, QuestionnaireRequest request) {
        Client client = clientRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new ValidationException(("Client not found")));

        QuestionnaireResponse questionnaire = questionnaireRepository.findByClient(client)
                .orElse(new QuestionnaireResponse());

        questionnaire.setClient(client);


        try {
            questionnaire.setMainReasons(objectMapper.writeValueAsString(request.getMainReasons()));
            questionnaire.setEmotionalStates(objectMapper.writeValueAsString(request.getEmotionalStates()));
            questionnaire.setAssociatedBehaviors(objectMapper.writeValueAsString(request.getAssociatedBehaviors()));
            questionnaire.setImpactLevel(objectMapper.writeValueAsString(request.getImpactLevel()));
            questionnaire.setMainReasonDuration(objectMapper.writeValueAsString(request.getMainReasonDuration()));
        } catch (JsonProcessingException e) {
            throw new ValidationException("Errore processamento dati questionario");
        }


        questionnaire.setEmotionalDurationMonths(request.getEmotionalDurationMonths());
        questionnaire.setBehaviorsDurationMonths(request.getBehaviorsDurationMonths());
        questionnaire.setGriefTimelineMonths(request.getGriefTimeLineMonths());
        questionnaire.setHasSocialSupport(request.getHasSocialSupport());
        questionnaire.setHasSocialSupport(request.getHasSocialSupport());
        questionnaire.setShowsDepressiveSymptoms(request.getShowDepressiveSymptoms());
        questionnaire.setDepressiveSymptomsMonths(request.getDepressiveSymptomsMonths());
        questionnaire.setAddictionType(request.getAddictionType());
        questionnaire.setIsInRehabilitation(request.getIsInRehabilitation());
        questionnaire.setViolentBehaviorsFrequency(request.getViolentBehavioursFrequency());

        QuestionnaireResponse saved = questionnaireRepository.save(questionnaire);

        List<Psychologist> recommended = matchingService.findRecommendedPsychologists(client);

        return QuestionnaireResultResponse.builder()
                .id(saved.getId())
                .calculatedPriority(saved.getCalculatedPriority())
                .requiredSpecialization(saved.getRequiredSpecialization())
                .completedAt(saved.getCompletedAt())
                .priorityMessage(getPriorityMessage(saved.getCalculatedPriority()))
                .specializationMessage(getSpecializationMessage(saved.getRequiredSpecialization()))
                .nextStepsMessage(getNextStepsMessage(saved.getCalculatedPriority()))
                .recommendedPsychologists(mapToRecommendations(recommended,client))
                .build();
    }

    @Transactional(readOnly = true)
    public QuestionnaireResponse getClientQuestionnaire(String clientEmail) {
        Client client = clientRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new ValidationException("Client not found"));
        return questionnaireRepository.findByClient(client)
                .orElseThrow(() -> new ValidationException("Questionnaire not compile"));
    }


    public String getPriorityMessage(Priority priority) {
        return switch (priority){
            case HIGH -> "La tua situazione richiede supporto immediato. Ti consigliamo di contattare un professionista entro 1 settimana";
            case MODERATE -> "La tua situazione richiedde attenzione. Ti consigliamo di fissare un appuntamento entro 2 settimane";
            case NORMAL -> "Un percorso di supporto psicologico può essere molto utile per il tuo benessere";
        };
    }

    public String getSpecializationMessage(PsychologistSpecialization spec) {
        return switch (spec) {
            case CHILD_PSYCHOLOGY -> "Hai bisogno di uno psicologo specializzato nell'età evolutiva.";
            case ADOLESCENT_PSYCHOLOGY -> "Hai bisogno di uno psicologo specializzato negli adolescenti.";
            case ADULT_PSYCHOLOGY -> "Hai bisogno di uno psicologo specializzato per adulti";
            case GERIATRIC_PSYCHOLOGY -> "Hai bisogno di uno psicogerontologo";
        };
    }

    public String getNextStepsMessage(Priority priority) {
        return switch (priority){
            case HIGH -> "Contatta immediatamente uno ddei professionisti suggeriti. In caso di emergenza, rivolgiti al pronto soccorso.";
            case MODERATE -> "Invia una richiesta a uno dei professionisti suggeriti per iniziare il percorso di supporto.";
            case NORMAL -> "Esplora i profili degli psicologi suggeriti e scegli quello più adatto alle tue esigenze";
        };
    }


    private List<PsychologistRecommendationResponse> mapToRecommendations(List<Psychologist> psychologists, Client client) {
        return psychologists.stream()
                .map(p -> PsychologistRecommendationResponse.builder()
                        .id(p.getId())
                        .fullName(p.getFullName())
                        .biography(p.getBiography())
                        .yearsExperience(p.getYearsExperience())
                        .specialization(p.getSpecializations().stream()
                                .map(PsychologistSpecialization::getDescription)
                                .collect(Collectors.toList())
                            )
                        .offersOnlineSessions(p.getOffersOnlineSessions())
                        .operatingCities(p.getOperatingLocations().stream()
                                .map(Location::getCity)
                                .collect(Collectors.toList())
                            )
                        .matchReason(getMatchReason(p, client))
                        .build()
                ).collect(Collectors.toList());
    }

    private String getMatchReason(Psychologist psychologist, Client client) {
        if(client.getLocation() != null && psychologist.operatesInCity(client.getLocation().getCity())){
            return "Opera nella tua città";
        }
        if(psychologist.getOffersOnlineSessions()) {
            return "Offre sessioni online";
        }
        return "Specializzazione compatibile";
    }

}
