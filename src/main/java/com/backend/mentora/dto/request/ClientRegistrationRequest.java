package com.backend.mentora.dto.request;


import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ClientRegistrationRequest {

    @NotBlank(message = "Email obbligatoria")
    @Email(message = "Email non valida")
    private String email;

    @NotBlank(message = "Password obbligatoria")
    @Size(min = 6, max = 100, message = "La password deve essere 6-100 caratteri")
    private String password;

    @NotBlank(message = "Nome obbligatorio")
    @Size(max = 50, message = "Massimo 50 caratteri per il nome")
    private String firstName;

    @NotBlank(message = "Cognome obbligatorio")
    @Size(max = 50, message = "Massimo 50 caratteri per il cognome")
    private String lastName;

    @NotNull(message = "Età obbligatoria")
    @Min(value = 1, message = "Età deve essere almeno di 1")
    @Max(value = 120, message = "Età deve essere al massimo di 120")
    private Integer age;

    @NotNull
    private String preferredSessionMode;

    private String phoneNumber;
    private String city;
    private String region;

}
