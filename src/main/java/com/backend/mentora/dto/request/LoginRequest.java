package com.backend.mentora.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email obbligatoria")
    @Email(message = "Email non valida")
    private String email;


    @NotBlank(message = "Password bbligatoria")
    @Size(min = 6, message = "Passwordd deve essere almeno di 6 caratteri")
    private String password;
}
