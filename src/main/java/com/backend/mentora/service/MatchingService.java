package com.backend.mentora.service;

import com.backend.mentora.entity.Client;
import com.backend.mentora.entity.Psychologist;
import com.backend.mentora.entity.QuestionnaireResponse;
import com.backend.mentora.entity.enums.PsychologistSpecialization;
import com.backend.mentora.entity.enums.SessionMode;
import com.backend.mentora.repository.PsychologistRepository;
import com.backend.mentora.repository.QuestionnaireResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchingService {

    private final PsychologistRepository psychologistRepository;
    private final QuestionnaireResponseRepository questionnaireRepository;

    public List<Psychologist> findRecommendedPsychologists(Client client) {
        QuestionnaireResponse questionnaire = questionnaireRepository.findByClient(client)
                .orElse(null);

        if(questionnaire == null)
            return findPsychologistsByAge(client.getAge());

        return findSuitablePsychologists(
                client.getLocation() != null ? client.getLocation().getCity() : null,
                questionnaire.getRequiredSpecialization(),
                client.getPreferredSessionMode()
                );

    }

    public List<Psychologist> findSuitablePsychologists(String city, PsychologistSpecialization specialization, SessionMode sessionMode) {
        if(city != null)
            return psychologistRepository.findSuitablePsychologists(city, specialization, sessionMode);
        else
            return psychologistRepository.findBySpecializationAndSessionMode(specialization, sessionMode);
    }


    private List<Psychologist> findPsychologists(String city,
                                                 PsychologistSpecialization specialization, SessionMode sessionMode) {

        List<Psychologist> candidates = psychologistRepository.findBySpecialization(specialization);

        return candidates.stream()
                .filter(p -> matchesSessionMode(p, sessionMode))
                .filter(p -> city == null || p.operatesInCity(city))
                .collect(Collectors.toList());
    }

    private boolean matchesSessionMode(Psychologist p, SessionMode mode) {
        if (mode == null || mode == SessionMode.INDIFFERENT) return true;
        return switch (mode) {
            case ONLINE -> p.getOffersOnlineSessions();
            case IN_PERSON -> p.getOffersInPersonSessions();
            case MIXED -> p.getOffersOnlineSessions() && p.getOffersInPersonSessions();
            default -> true;
        };
    }




    private List<Psychologist> findPsychologistsByAge(Integer age) {
        PsychologistSpecialization specialization = PsychologistSpecialization.getByAge(age);
        return psychologistRepository.findBySpecialization(specialization);
    }

}
