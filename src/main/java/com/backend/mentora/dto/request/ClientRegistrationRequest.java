package com.backend.mentora.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    private Integer age;

    private String phoneNumber;
    private String city;
    private String region;

}
