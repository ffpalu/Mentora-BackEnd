package com.backend.mentora.controller;


import com.backend.mentora.dto.request.UpdateProfileRequest;
import com.backend.mentora.dto.response.ClientRequestResponse;
import com.backend.mentora.dto.response.ProfileResponse;
import com.backend.mentora.dto.response.UserResponse;
import com.backend.mentora.entity.enums.RequestStatus;
import com.backend.mentora.service.MatchingService;
import com.backend.mentora.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
		private final MatchingService matchingService;


    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getCurrentUserProfile(Authentication authentication) {
        ProfileResponse profileResponse = userService.getUserProfile(authentication.getName());
        return ResponseEntity.ok(profileResponse);
    }

    @PutMapping("/profile")
    public ResponseEntity<ProfileResponse> getCurrentUserProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication auth
            ) {
        ProfileResponse updated = userService.updateUserProfile(auth.getName(), request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById (@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/account")
    public ResponseEntity<String> deactivateAccount(Authentication auth) {
        userService.deactivateUser(auth.getName());
        return ResponseEntity.ok("User has been deactivated");
    }

    @PutMapping("/password")
    public ResponseEntity<String> changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            Authentication auth) {

        userService.changePassword(auth.getName(), currentPassword, newPassword);
        return ResponseEntity.ok("Password aggiornata con successo");
    }


    @GetMapping("/clients")
    @PreAuthorize("hasRole('PSYCHOLOGIST')")
    public ResponseEntity<List<ClientRequestResponse>> getMyClients(Authentication auth) {
				List<ClientRequestResponse> response = matchingService.getClientRequests(auth.getName(), RequestStatus.ACCEPTED);
        return ResponseEntity.ok(response);
    }

		@GetMapping("/myrequests")
		@PreAuthorize("hasRole('CLIENT')")
		public ResponseEntity<List<ClientRequestResponse>> getMyRequests(Authentication auth) {

				List<ClientRequestResponse> response = matchingService.getClientRequests(auth.getName());

				return ResponseEntity.ok(response);
		}

}
