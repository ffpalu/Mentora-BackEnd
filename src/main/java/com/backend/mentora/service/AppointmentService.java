package com.backend.mentora.service;

import com.backend.mentora.dto.request.AppointmentRequest;
import com.backend.mentora.dto.response.AppointmentResponse;
import com.backend.mentora.entity.Appointment;
import com.backend.mentora.entity.Client;
import com.backend.mentora.entity.Psychologist;
import com.backend.mentora.entity.enums.AppointmentStatus;
import com.backend.mentora.exception.ValidationException;
import com.backend.mentora.repository.AppointmentRepository;
import com.backend.mentora.repository.ClientRepository;
import com.backend.mentora.repository.PsychologistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {
	private final AppointmentRepository appointmentRepository;
	private final ClientRepository clientRepository;
	private final PsychologistRepository psychologistRepository;


	public AppointmentResponse createAppointment(String userEmail, AppointmentRequest request) {
		Client client = clientRepository.findByEmail(userEmail)
						.orElseThrow(() -> new ValidationException("Client not found"));

		Psychologist psychologist = psychologistRepository.findById(request.getPsychologistId())
						.orElseThrow(() -> new ValidationException("Psychologist not found"));

		Appointment appointment = new Appointment();
		appointment.setClient(client);
		appointment.setPsychologist(psychologist);
		appointment.setAppointmentDateTime(request.getAppointmentDateTime());
		appointment.setDurationMinutes(request.getDurationMinutes());
		appointment.setSessionMode(request.getSessionMode());
		appointment.setNotes(request.getNotes());
		appointment.setStatus(AppointmentStatus.REQUESTED);

		Appointment saved = appointmentRepository.save(appointment);

		return mapToResponse(saved);

	}


	@Transactional(readOnly = true)
	public List<AppointmentResponse> getUserAppointments(String userEmail, int page, int size) {
		PageRequest pageRequest = PageRequest.of(page, size);

		List<Appointment> appointments = appointmentRepository.findByUserEmailOrderByAppointmentDateTimeDesc(userEmail, pageRequest);

		return appointments.stream().map(this::mapToResponse).toList();

	}



	public void updateAppointmentStatus(Long appointmentId, AppointmentStatus status, String userEmail) {
		Appointment appointment = appointmentRepository.findById(appointmentId)
						.orElseThrow(() -> new ValidationException("Appointment not found"));

		boolean isAuthorized = appointment.getClient().getEmail().equals(userEmail)||
						appointment.getPsychologist().getEmail().equals(userEmail);
		if (!isAuthorized) {
			throw new ValidationException("You are not authorized to update this appointment");
		}

		appointment.setStatus(status);
		appointmentRepository.saveAndFlush(appointment);


	}

	private AppointmentResponse mapToResponse(Appointment appointment) {
		return AppointmentResponse.builder()
						.id(appointment.getId())
						.clientName(appointment.getClient().getFullName())
						.psychologistName(appointment.getPsychologist().getFullName())
						.appointmentDateTime(appointment.getAppointmentDateTime())
						.durationMinutes(appointment.getDurationMinutes())
						.sessionMode(appointment.getSessionMode())
						.status(appointment.getStatus())
						.locationCity(appointment.getLocation() != null ? appointment.getLocation().getCity() : null)
						.onlineMeetingUrl(appointment.getOnlineMeetingUrl())
						.notes(appointment.getNotes())
						.clientNotes(appointment.getClientNotes())
						.build();
	}

}
