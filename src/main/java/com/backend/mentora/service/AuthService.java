package com.backend.mentora.service;

import com.backend.mentora.dto.request.ClientRegistrationRequest;
import com.backend.mentora.dto.request.LoginRequest;
import com.backend.mentora.dto.request.PsychologistRegistrationRequest;
import com.backend.mentora.dto.response.JwtResponse;
import com.backend.mentora.dto.response.UserResponse;
import com.backend.mentora.entity.Client;
import com.backend.mentora.entity.Location;
import com.backend.mentora.entity.Psychologist;
import com.backend.mentora.entity.User;
import com.backend.mentora.exception.ValidationException;
import com.backend.mentora.repository.ClientRepository;
import com.backend.mentora.repository.LocationRepository;
import com.backend.mentora.repository.PsychologistRepository;
import com.backend.mentora.repository.UserRepository;
import com.backend.mentora.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PsychologistRepository psychologistRepository;
    private final LocationRepository locationRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ValidationException("User not found"));

        UserResponse userResponse = mapToUserResponse(user);

        return JwtResponse.builder()
                .token(token)
                .user(userResponse)
                .build();
    }

    public UserResponse registerClient(ClientRegistrationRequest request) {
        if(userRepository.existsByEmail(request.getEmail()))
            throw new ValidationException("Email already exists");

        Location location = findOrCreateLocation(request.getCity(), request.getRegion());

        Client client = new Client(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFirstName(),
                request.getLastName(),
                request.getAge()
        );

        client.setPhoneNumber(request.getPhoneNumber());
        client.setLocation(location);

        Client savedClient = clientRepository.save(client);

        return mapToUserResponse(savedClient);
    }

    public UserResponse registerPsychologist(PsychologistRegistrationRequest request) {

        if(userRepository.existsByEmail(request.getEmail()))
            throw new ValidationException("Email already exists");

        Location location = findOrCreateLocation(request.getCity(), request.getRegion());

        Psychologist psychologist = new Psychologist(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFirstName(),
                request.getLastName(),
                request.getLicenseNumber()
        );

        psychologist.setPhoneNumber(request.getPhoneNumber());
        psychologist.setBiography(request.getBiography());
        psychologist.setYearsExperience(request.getYearsExperience());
        psychologist.setOffersOnlineSession(request.getOffersOnlineSession());
        psychologist.setOffersInPersonSession(request.getOffersInPersonSession());
        psychologist.getOperatingLocations().add(location);

        Psychologist savedPsychologist = psychologistRepository.save(psychologist);

        return mapToUserResponse(savedPsychologist);


    }





    private Location findOrCreateLocation(String city, String region){
        if(city == null || region == null){
            return null;
        }

        return locationRepository.findByCityAndRegion(city,region).orElseGet(() -> {
            Location newLocation = new Location(city, region, "Italia");
            return locationRepository.save(newLocation);
        });

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
