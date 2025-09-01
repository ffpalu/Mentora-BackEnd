package com.backend.mentora.service;

import com.backend.mentora.dto.request.UpdateProfileRequest;
import com.backend.mentora.dto.response.ProfileResponse;
import com.backend.mentora.dto.response.UserResponse;
import com.backend.mentora.entity.Client;
import com.backend.mentora.entity.Location;
import com.backend.mentora.entity.Psychologist;
import com.backend.mentora.entity.User;
import com.backend.mentora.entity.enums.SessionMode;
import com.backend.mentora.exception.UnauthorizedException;
import com.backend.mentora.exception.ValidationException;
import com.backend.mentora.repository.ClientRepository;
import com.backend.mentora.repository.LocationRepository;
import com.backend.mentora.repository.PsychologistRepository;
import com.backend.mentora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PsychologistRepository psychologistRepository;
    private final LocationRepository locationRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public ProfileResponse getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ValidationException("User not found"));
        return mapToProfileResponse(user);
    }

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


    public ProfileResponse updateUserProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ValidationException("User not found"));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }


        if (user instanceof Client client) {
            updateClientSpecificFields(client, request);
        }
        else if (user instanceof Psychologist psychologist) {
            updatePsychologistSpecificFields(psychologist, request);
        }

        User savedUser = userRepository.save(user);
        return  mapToProfileResponse(savedUser);


    }

    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ValidationException("User not found"));

        if(!passwordEncoder.matches(currentPassword, user.getPassword())){
            throw new UnauthorizedException("Wrong password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void deactivateUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ValidationException("User not found"));

        user.setIsActive(false);
        userRepository.save(user);
    }

    private void updateClientSpecificFields(Client client, UpdateProfileRequest request) {
        if (request.getCity() != null && request.getRegion() != null) {
            Location location = findOrCreateLocation(request.getCity(), request.getRegion());
            client.setLocation(location);
        }
				if (request.getPreferredSessionMode() != null) {
					SessionMode sessionMode;
					switch (request.getPreferredSessionMode()) {
						case "ONLINE":
							sessionMode = SessionMode.ONLINE;
							break;
						case "IN_PERSON":
							sessionMode = SessionMode.IN_PERSON;
							break;
						case "MIXED":
							sessionMode = SessionMode.MIXED;
							break;
						case "INDIFFERENT":
							sessionMode = SessionMode.INDIFFERENT;
							break;
						default:
							sessionMode = SessionMode.INDIFFERENT;
					}
					client.setPreferredSessionMode(sessionMode);
				}
    }

    private void updatePsychologistSpecificFields(Psychologist psychologist, UpdateProfileRequest request) {
        if(request.getBiography() != null) {
            psychologist.setBiography(request.getBiography());
        }
        if(request.getYearsExperience() != null) {
            psychologist.setYearsExperience(request.getYearsExperience());
        }
        if(request.getCity() != null && request.getRegion() != null) {
            Location location = findOrCreateLocation(request.getCity(), request.getRegion());
            psychologist.getOperatingLocations().add(location);
        }
				if(request.getPreferredSessionMode() != null){
					psychologist.setOffersOnlineSessions(request.getPreferredSessionMode().equals("ONLINE") || request.getPreferredSessionMode().equals("INDIFFERENT") || request.getPreferredSessionMode().equals("MIXED"));
					psychologist.setOffersInPersonSessions(request.getPreferredSessionMode().equals("IN_PERSON") || request.equals("INDIFFERENT") || request.getPreferredSessionMode().equals("MIXED"));
				}
    }

    private Location findOrCreateLocation(String city, String region){
        return locationRepository.findByCityAndRegion(city, region)
                .orElseGet(() -> {
                    Location newLocation = new Location(city,region, "Italia");
                    return locationRepository.save(newLocation);
                });
    }

    private ProfileResponse mapToProfileResponse(User user) {
        ProfileResponse.ProfileResponseBuilder profileResponseBuilder = ProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .isActive(user.getIsActive());

        if(user instanceof Client client) {
            profileResponseBuilder
                    .age(client.getAge())
                    .preferredSessionMode(client.getPreferredSessionMode())
                    .hasCompletedQuestionnaire(client.hasCompletedQuestionnaire());

            if(client.getLocation() != null) {
                profileResponseBuilder
                        .city(client.getLocation().getCity())
                        .region(client.getLocation().getRegion());
            }

        }

        else if(user instanceof Psychologist psychologist) {
            profileResponseBuilder
                    .licenseNumber(psychologist.getLicenseNumber())
                    .biography(psychologist.getBiography())
                    .yearsExperience(psychologist.getYearsExperience())
                    .hourlyRate(psychologist.getHourlyRate())
                    .offersOnlineSessions(psychologist.getOffersOnlineSessions())
                    .offersInPersonSessions(psychologist.getOffersInPersonSessions())
                    .specializations(psychologist.getSpecializations())
                    .operatingCites(psychologist.getOperatingLocations().stream()
                            .map(Location::getCity)
                            .collect(Collectors.toSet()));
        }
        return profileResponseBuilder.build();

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
