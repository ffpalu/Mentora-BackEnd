package com.backend.mentora.dto.response;


import com.backend.mentora.entity.enums.PsychologistSpecialization;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
public class PsychologistProfileResponse {
	private Long id;
	private String firstName;
	private String lastName;
	private String fullName;
	private String biography;
	private String licenceNumber;
	private Integer yearsExperience;
	private BigDecimal hourlyRate;
	private Boolean offersOnlineSessions;
	private Boolean offersInPersonSessions;
	private Set<PsychologistSpecialization> specializations;
	private Set<String> operatingCities;
	private Boolean isAvailable;
}
