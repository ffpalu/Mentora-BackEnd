package com.backend.mentora.entity;

import com.backend.mentora.entity.enums.AppointmentStatus;
import com.backend.mentora.entity.enums.SessionMode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "client_id", nullable = false)
	private Client client;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "psychologist_id", nullable = false)
	private Psychologist psychologist;

	@Column(name = "appointment_datetime", nullable = false)
	private LocalDateTime appointmentDateTime;

	@Column(name = "duration_minutes", nullable = false)
	private Integer durationMinutes = 50;

	@Enumerated(EnumType.STRING)
	@Column(name = "session_mode", nullable = false)
	private SessionMode sessionMode;

	@Enumerated(EnumType.STRING)
	@Column( nullable = false)
	private AppointmentStatus status = AppointmentStatus.REQUESTED;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "location_id")
	private Location location;

	@Column(name = "online_meeting_url")
	private String onlineMeetingUrl;

	@Column(name = "client_notes", length = 1000)
	private String clientNotes;

	@Column(columnDefinition = "TEXT")
	private String notes;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@PrePersist
	protected void  onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}
	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

}
