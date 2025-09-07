package com.backend.mentora.controller;

import com.backend.mentora.dto.request.AppointmentRequest;
import com.backend.mentora.dto.response.AppointmentResponse;
import com.backend.mentora.entity.enums.AppointmentStatus;
import com.backend.mentora.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

	private final AppointmentService appointmentService;

	@PostMapping
	@PreAuthorize("hasRole('CLIENT')")
	public ResponseEntity<AppointmentResponse> createAppointment(
					@Valid @RequestBody AppointmentRequest request,
					Authentication auth
					) {

		AppointmentResponse response = appointmentService.createAppointment(auth.getName(), request);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<List<AppointmentResponse>> getAppointments(
					@RequestParam(defaultValue = "0") int page,
					@RequestParam(defaultValue = "10") int size,
					Authentication auth
					) {
		List<AppointmentResponse> response = appointmentService.getUserAppointments(auth.getName(), page, size);
		return ResponseEntity.ok(response);
	}


	@PutMapping("/{appointmentId}/status")
	public ResponseEntity<String> updateStatus(
					@PathVariable Long appointmentId,
					@RequestParam AppointmentStatus status,
					Authentication auth
					){
		appointmentService.updateAppointmentStatus(appointmentId, status, auth.getName());
		return ResponseEntity.ok("Appointment status updated to " + status);
	}







}
