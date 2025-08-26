package com.backend.mentora.controller;


import com.backend.mentora.dto.request.ClientRegistrationRequest;
import com.backend.mentora.dto.request.LoginRequest;
import com.backend.mentora.dto.request.PsychologistRegistrationRequest;
import com.backend.mentora.dto.response.UserResponse;
import com.backend.mentora.entity.User;
import com.backend.mentora.repository.UserRepository;
import com.backend.mentora.dto.response.JwtResponse;
import com.backend.mentora.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.login(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/register/client")
    public ResponseEntity<UserResponse> registerClient(@Valid @RequestBody ClientRegistrationRequest request) {
        UserResponse userResponse = authService.registerClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PostMapping("/register/psychologist")
    public ResponseEntity<UserResponse> registerPsychologist(@Valid @RequestBody PsychologistRegistrationRequest request) {
        UserResponse userResponse = authService.registerPsychologist(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        if(authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .isActive(user.getIsActive())
                .build();

        return ResponseEntity.ok(response);

    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logout effettuato");
    }

}
