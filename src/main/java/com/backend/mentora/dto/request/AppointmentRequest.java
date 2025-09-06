package com.backend.mentora.dto.request;

import com.backend.mentora.entity.enums.SessionMode;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentRequest {
	@NotNull
	private Long psychologistId;

	@NotNull
	@Future
	private LocalDateTime appointmentDateTime;

	private Integer durationMinutes = 50;

	@NotNull
	private SessionMode sessionMode;

	private String location; // Optional, required if sessionMode is IN_PERSON
	private String notes;
}
