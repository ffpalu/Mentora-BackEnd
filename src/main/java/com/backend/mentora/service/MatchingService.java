package com.backend.mentora.service;

import com.backend.mentora.dto.response.ClientRequestResponse;
import com.backend.mentora.dto.response.PsychologistProfileResponse;
import com.backend.mentora.entity.*;
import com.backend.mentora.entity.enums.PsychologistSpecialization;
import com.backend.mentora.entity.enums.RequestStatus;
import com.backend.mentora.entity.enums.SessionMode;
import com.backend.mentora.exception.ValidationException;
import com.backend.mentora.repository.ClientPsychologistRelationRepository;
import com.backend.mentora.repository.ClientRepository;
import com.backend.mentora.repository.PsychologistRepository;
import com.backend.mentora.repository.QuestionnaireResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchingService {

    private final PsychologistRepository psychologistRepository;
		private final ClientRepository clientRepository;
    private final QuestionnaireResponseRepository questionnaireRepository;
		private final ClientPsychologistRelationRepository relationRepository;



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
                .filter(p -> matchesSessionMode(p, sessionMode.toString()))
                .filter(p -> city == null || p.operatesInCity(city))
                .collect(Collectors.toList());
    }

		@Transactional(readOnly = true)
		public List<PsychologistProfileResponse> searchPsychologists(String clientEmail, String city, String specialization, String sessionMode) {
			Client client = clientRepository.findByEmail(clientEmail)
							.orElseThrow(() -> new ValidationException("User not found"));

			List<Psychologist> psychologists;

			if(city != null || specialization != null || sessionMode != null) {
				psychologists = findByFilters(city, specialization, sessionMode);
			} else {
				psychologists = findRecommendedPsychologists(client);
			}


			Set<Long> connectedIds = relationRepository.findByClientAndIsActive(client, true)
							.stream().map(r -> r.getPsychologist().getId()).collect(Collectors.toSet());

			return psychologists.stream()
							.filter(p -> !connectedIds.contains(p.getId()))
							.map(this::mapToPsychologistProfile)
							.collect(Collectors.toList());
			
		}


		@Transactional(readOnly = true)
		public PsychologistProfileResponse getPsychologistProfile(Long id) {
			Psychologist psychologist = psychologistRepository.findById(id)
							.orElseThrow(() -> new ValidationException("Psychologist with id " + id + " not found"));

			return mapToPsychologistProfile(psychologist);
		}

		public void sendTherapyRequest(String clientEmail, Long psychologistId, String message) {
			Client client = clientRepository.findByEmail(clientEmail)
							.orElseThrow(() -> new ValidationException("Client with email " + clientEmail + " not found"));

			Psychologist psychologist = psychologistRepository.findById(psychologistId)
							.orElseThrow(() -> new ValidationException("Psychologist with id " + psychologistId + " not found"));

			boolean exists = relationRepository.existsByClientAndPsychologistAndIsActive(client, psychologist, true);


			if(exists) {
				throw new ValidationException("You have already sent a request to this psychologist");
			}

			ClientPsychologistRelation relation = new ClientPsychologistRelation();
			relation.setClient(client);
			relation.setPsychologist(psychologist);
			relation.setClientMessage(message);
			relation.setStatus(RequestStatus.PENDING);
			relation.setIsActive(true);

			relationRepository.save(relation);
		}


		@Transactional(readOnly = true)
		public List<ClientRequestResponse> getClientRequests(String clientEmail) {
			Client client = clientRepository.findByEmail(clientEmail)
							.orElseThrow(() -> new ValidationException("Client with email " + clientEmail + " not found"));


			List<ClientPsychologistRelation> requests = relationRepository.findByClientAndIsActive(client, true);

			return requests.stream()
							.map(this::mapToClientRequest)
							.collect(Collectors.toList());
		}

		@Transactional(readOnly = true)
		public  List<ClientRequestResponse> getClientRequests(String psychologistEmail, RequestStatus requestStatus) {
			Psychologist psychologist = psychologistRepository.findByEmail(psychologistEmail)
							.orElseThrow(() -> new ValidationException("Psychologist with email " + psychologistEmail + " not found"));

			List<ClientPsychologistRelation> requests = relationRepository.findByPsychologistAndStatus(psychologist, requestStatus);

			return requests.stream()
							.map(this::mapToClientRequest)
							.collect(Collectors.toList());
		}

		@Transactional
		public void respondToTherapyRequest(String psychologistEmail, Long requestId, RequestStatus status, String note) {

			Psychologist psychologist = psychologistRepository.findByEmail(psychologistEmail)
							.orElseThrow(() -> new ValidationException("Psychologist with email " + psychologistEmail + " not found"));

			ClientPsychologistRelation relation = relationRepository.findById(requestId)
							.orElseThrow(() -> new ValidationException("Request with id " + requestId + " not found"));

			if(!relation.getPsychologist().getId().equals(psychologist.getId())) {
				throw new ValidationException("Not authorized to respond to this request");
			}


			if(status == RequestStatus.ACCEPTED) {
				relation.accept(note);
			} else if (status == RequestStatus.REJECTED) {
				relation.reject(note);
			}


			relationRepository.saveAndFlush(relation);


		}

		@Transactional(readOnly = true)
		public List<Psychologist> findRecommendedPsychologists(Client client) {
			QuestionnaireResponse questionnaire = questionnaireRepository.findByClient(client)
							.orElse(null);

			if (questionnaire == null) {
				PsychologistSpecialization spec = PsychologistSpecialization.getByAge(client.getAge());
				return psychologistRepository.findBySpecialization(spec);
			}

			return findByFilters(
							client.getLocation() != null ? client.getLocation().getCity() : null,
							questionnaire.getRequiredSpecialization().name(),
							client.getPreferredSessionMode() != null ? client.getPreferredSessionMode().name() : null
			);

		}

		private List<Psychologist> findByFilters(String city, String specialization, String sessionMode) {
			List<Psychologist> candidates = psychologistRepository.findAll();

			return candidates.stream()
							.filter(p -> p.getIsActive())
							.filter(p -> city == null || p.operatesInCity(city))
							.filter(p -> specialization == null || hasSpecialization(p, specialization))
							.filter(p -> sessionMode == null || matchesSessionMode(p, sessionMode))
							.collect(Collectors.toList());
		}

    private boolean matchesSessionMode(Psychologist p, String mode) {
        try {
					SessionMode sessionMode = SessionMode.valueOf(mode.toUpperCase());
					return switch (mode) {
						case "IN_PERSON" -> p.getOffersInPersonSessions();
						case "ONLINE" -> p.getOffersOnlineSessions();
						case "MIXED" -> p.getOffersInPersonSessions() && p.getOffersOnlineSessions();
						case "INDIFFERENT" -> true;
						default -> false;
					};
				}
				catch (IllegalArgumentException e) {
					return false;
				}
    }

		private Boolean hasSpecialization(Psychologist p, String specialization) {
			try {
				PsychologistSpecialization spec = PsychologistSpecialization.valueOf(specialization.toUpperCase());
				return  p.getSpecializations().contains(spec);
			}
			catch (IllegalArgumentException e) {
				return false;
			}
		}


    private List<Psychologist> findPsychologistsByAge(Integer age) {
        PsychologistSpecialization specialization = PsychologistSpecialization.getByAge(age);
        return psychologistRepository.findBySpecialization(specialization);
    }




		private PsychologistProfileResponse mapToPsychologistProfile(Psychologist p) {
			List<Location> location = new ArrayList<>(p.getOperatingLocations());

			List<String> cities = new ArrayList<>();
			List<String> regions = new ArrayList<>();
			for(Location loc : location){
				cities.add(loc.getCity());
				regions.add(loc.getRegion());
			}

			return PsychologistProfileResponse.builder()
							.id(p.getId())
							.firstName(p.getFirstName())
							.lastName(p.getLastName())
							.fullName(p.getFullName())
							.biography(p.getBiography())
							.licenceNumber(p.getLicenseNumber())
							.yearsExperience(p.getYearsExperience())
							.hourlyRate(p.getHourlyRate())
							.offersOnlineSessions(p.getOffersOnlineSessions())
							.offersInPersonSessions(p.getOffersInPersonSessions())
							.specializations(p.getSpecializations())
							.operatingCities(cities)
							.operatingRegions(regions)
							.isAvailable(p.getIsActive())
							.build();
		}

		private ClientRequestResponse mapToClientRequest(ClientPsychologistRelation relation) {

			Client client = relation.getClient();
			String priority = "NORMAL";
			if(client.getQuestionnaireResponse() != null)
				priority = client.getQuestionnaireResponse().getCalculatedPriority().name();

			return ClientRequestResponse.builder()
							.id(relation.getId())
							.clientId(client.getId())
							.clientName(client.getFullName())
							.clientAge(client.getAge())
							.clientMessage(relation.getClientMessage())
							.status(relation.getStatus())
							.requestedAt(relation.getRequestedAt())
							.clientPriority(priority)
							.psychologistName(relation.getPsychologist().getFullName())
							.psychologistFeedback(relation.getNotes())
							.build();

		}

}
