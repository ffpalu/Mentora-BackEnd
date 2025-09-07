package com.backend.mentora.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(max = 50, message = "Nome massimo 50 caratteri")
    private String firstName;

    @Size(max = 50, message = "Cognome massimo 50 caratteri")
    private String lastName;

    @Size(max = 20, message = "Telefono massimo 20 caratteri")
    private String phoneNumber;

    @Size(max = 1000, message = "Biografia massimo 1000 caratteri")
    private String biography;

    private Integer yearsExperience;
    private String city;
    private String region;

    private String operatingCities;
    private String operatingRegions;

    private String preferredSessionMode;

}
