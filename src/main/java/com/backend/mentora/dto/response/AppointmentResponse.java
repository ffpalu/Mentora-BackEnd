package com.backend.mentora.dto.response;

import com.backend.mentora.entity.enums.AppointmentStatus;
import com.backend.mentora.entity.enums.SessionMode;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AppointmentResponse {
	private Long id;
	private String clientName;
	private String psychologistName;
	private LocalDateTime appointmentDateTime;
	private Integer durationMinutes;
	private SessionMode sessionMode;
	private AppointmentStatus status;
	private String locationCity;
	private String onlineMeetingUrl;
	private String notes;
	private String clientNotes;
}
