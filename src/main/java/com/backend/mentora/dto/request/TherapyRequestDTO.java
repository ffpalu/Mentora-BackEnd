package com.backend.mentora.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TherapyRequestDTO {
	@Size(max = 500, message = "Messaggio massimo 500 caratteri")
	private String message;
}
