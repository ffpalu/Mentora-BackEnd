package com.backend.mentora.controller;


import com.backend.mentora.dto.request.TherapyRequestDTO;
import com.backend.mentora.dto.response.ClientRequestResponse;
import com.backend.mentora.dto.response.PsychologistProfileResponse;
import com.backend.mentora.entity.enums.RequestStatus;
import com.backend.mentora.service.MatchingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/psychologists")
@RequiredArgsConstructor
public class PsychologistController {
	private final MatchingService matchingService;

	@GetMapping("/search")
	@PreAuthorize("hasRole('CLIENT')")
	public ResponseEntity<List<PsychologistProfileResponse>> searchPsychologists(
					@RequestParam(required = false) String city,
					@RequestParam(required = false) String specialization,
					@RequestParam(required = false) String sessionMode,
					Authentication auth
		){
		List<PsychologistProfileResponse> responses = matchingService.searchPsychologists(auth.getName(), city, specialization, sessionMode);

		return ResponseEntity.ok(responses);
	}

	@GetMapping("/{id}")
	public ResponseEntity<PsychologistProfileResponse> getPsuchologistProfile(@PathVariable Long id){
		PsychologistProfileResponse response = matchingService.getPsychologistProfile(id);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/{psychologistId}/request")
	@PreAuthorize("hasRole('CLIENT')")
	public ResponseEntity<String> sendTherapyRequest(
					@PathVariable Long psychologistId,
					@Valid @RequestBody TherapyRequestDTO request,
					Authentication auth
					){
		 matchingService.sendTherapyRequest(auth.getName(), psychologistId, request.getMessage());
		 return ResponseEntity.ok("Request sent successfully");
	}

	@GetMapping("/requests")
	@PreAuthorize("hasRole('PSYCHOLOGIST')")
	public ResponseEntity<List<ClientRequestResponse>> getClientRequests(Authentication auth) {
		List<ClientRequestResponse> request = matchingService.getClientRequests(auth.getName());
		return ResponseEntity.ok(request);
	}

	@PutMapping("/requests/{requestId}")
	@PreAuthorize("hasRole('PSYCHOLOGIST')")
	public ResponseEntity<String> respondToRequest(
					@PathVariable Long requestId,
					@RequestParam RequestStatus status,
					@RequestParam(required = false) String notes,
					Authentication auth
					){
		matchingService.respondToTherapyRequest(auth.getName(), requestId, status, notes);
		return ResponseEntity.ok("Response sent successfully");
	}


}
