package com.backend.mentora.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PsychologistRegistrationRequest {

    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Email non valida")
    private String email;

    @NotBlank(message = "Password è obbligatoria")
    @Size(min = 6, max = 100, message = "La password deve essere in 6-100 caratteri")
    private String password;

    @NotBlank(message = "Il nome è obbligatorio")
    private String firstName;

    @NotBlank(message = "Il cognome è obbligatorio")
    private String lastName;

    @NotBlank(message = "Il numero di licenza è obbligatoria")
    private String licenseNumber;


    private String biography;
    private Integer yearsExperience;
    private String phoneNumber;

    private String city;
    private String region;

    private Boolean offersOnlineSession = false;
    private Boolean offersInPersonSession = false;




}
