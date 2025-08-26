package com.backend.mentora.service;

import com.backend.mentora.dto.response.UserResponse;
import com.backend.mentora.entity.Client;
import com.backend.mentora.entity.Psychologist;
import com.backend.mentora.entity.User;
import com.backend.mentora.exception.ValidationException;
import com.backend.mentora.repository.ClientRepository;
import com.backend.mentora.repository.PsychologistRepository;
import com.backend.mentora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PsychologistRepository psychologistRepository;

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ValidationException("User not found"));

        return mapToUserResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ValidationException("User not found"));
        return mapToUserResponse(user);
    }

    @Transactional
    public Optional<Client> getClientByEmail(String email) {
        return clientRepository.findByEmail(email);
    }

    @Transactional
    public Optional<Psychologist> getPsychologistByEmail(String email) {
        return psychologistRepository.findByEmail(email);
    }


    public void deactivateUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ValidationException("User not found"));

        user.setIsActive(false);
        userRepository.save(user);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .isActive(user.getIsActive())
                .build();
    }



}
