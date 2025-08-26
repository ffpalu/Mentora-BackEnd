package com.backend.mentora.controller;

import com.backend.mentora.entity.Client;
import com.backend.mentora.entity.Psychologist;
import com.backend.mentora.entity.User;
import com.backend.mentora.repository.ClientRepository;
import com.backend.mentora.repository.LocationRepository;
import com.backend.mentora.repository.PsychologistRepository;
import com.backend.mentora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PsychologistRepository psychologistRepository;
    private final LocationRepository locationRepository;


    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return  ResponseEntity.ok("Hello from Mentora Backend! \uD83D\uDE80");
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus(){
        Map<String, Object> status = new HashMap<>();

        status.put("status", "OK");
        status.put("timestamp", LocalDateTime.now().toString());
        status.put("message", "Mentora Backend has been started!");

        status.put("database", Map.of(
                "totalUsers", userRepository.count(),
                "clients", clientRepository.count(),
                "psychologists", psychologistRepository.count(),
                "locations", locationRepository.count()
        ));

        return ResponseEntity.ok(status);

    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUser(){
        List<Client> clients = clientRepository.findAll();
        List<Psychologist> psychologists = psychologistRepository.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put("clients", clients.stream().map(this::mapClientToResponse).toList());
        response.put("psychologists", psychologists.stream().map(this::mapPsychologistToResponse).toList());

        return ResponseEntity.ok(response);

    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id){
        Optional<User> user = userRepository.findById(id);

        if(user.isEmpty())
            return ResponseEntity.notFound().build();

        Map<String, Object> response = new HashMap<>();

        User u = user.get();

        response.put("id",  u.getId());
        response.put("email", u.getEmail());
        response.put("fullName", u.getFullName());
        response.put("role", u.getRole());
        response.put("isActive", u.getIsActive());
        response.put("createdAt", u.getCreatedAt());

        if (u instanceof Client client){
            response.put("type", "CLIENT");
            response.put("age", client.getAge());
            response.put("preferredSessionMode",  client.getPreferredSessionMode());
            response.put("hasQuestionnaire", client.hasCompletedQuestionnaire());
        } else if (u instanceof Psychologist psychologist) {
            response.put("type", "PSYCHOLOGIST");
            response.put("licenseNumber", psychologist.getLicenseNumber());
            response.put("yearsExperience", psychologist.getYearsExperience());
            response.put("specializations",  psychologist.getSpecializations());
            response.put("offersOnline", psychologist.getOffersOnlineSession());
        }

        return ResponseEntity.ok(response);

    }

    @GetMapping("/clients")
    public ResponseEntity<List<Map<String, Object>>> getClient(){

        List<Client> clients = clientRepository.findAll();
        List<Map<String, Object>> response = clients.stream()
                .map(this::mapClientToResponse)
                .toList();

        return ResponseEntity.ok(response);
    }


    @GetMapping("/psychologists")
    public ResponseEntity<List<Map<String, Object>>> getPsychologist () {
        List<Psychologist> psychologist = psychologistRepository.findAll();
        List<Map<String, Object>> response = psychologist.stream()
                .map(this::mapPsychologistToResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/psychologists/email/{email}")
    public ResponseEntity<Map<String, Object>> getPsychologistByEmail(@PathVariable String email) {
        Optional<Psychologist> psychologist = psychologistRepository.findByEmail(email);
        return psychologist.map(value -> ResponseEntity.ok(mapPsychologistToResponse(value))).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/database")
    public ResponseEntity<Map<String, Object>> getDatabaseInfo() {
        Map<String, Object> response = new HashMap<>();

        try {
            response.put("users", userRepository.count());
            response.put("clients", clientRepository.count());
            response.put("psychologists", psychologistRepository.count());
            response.put("locations", locationRepository.count());


            response.put("testQueries", Map.of(
                    "userByEmail", userRepository.findByEmail("dr.rossi@mentora.com").isPresent(),
                    "clientByEmail", clientRepository.findByEmail("anna.neri@email.com").isPresent(),
                    "allLocations",  locationRepository.findAll().size()
            ));

            response.put("status", "Database OK");

        }
        catch (Exception e) {
            response.put("status", "Database ERROR");
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/reset")
    public ResponseEntity<Map<String, String>> resetData (){
        try {
            userRepository.deleteAll();
            locationRepository.deleteAll();

            return ResponseEntity.ok(Map.of(
                    "status", "OK",
                    "message", "Database pulito. Riavvia l'app per rigenerare i dati di test"
            ));
        }
        catch (Exception e){
            return ResponseEntity.ok(Map.of(
                    "status", "ERROR",
                    "message", "Errore durante il reset: " +e.getMessage()
            ));
        }
    }

    private Map<String, Object> mapClientToResponse(Client client){
        Map<String, Object> response = new HashMap<>();

        response.put("id", client.getId());
        response.put("email", client.getEmail());
        response.put("fullName", client.getFullName());
        response.put("age", client.getAge());
        response.put("preferredSessionMode", client.getPreferredSessionMode());
        response.put("location", client.getLocation() != null ? client.getLocation().getCity() : null);
        response.put("hasQuestionnaire", client.hasCompletedQuestionnaire());
        response.put("isActive", client.getIsActive());

        return response;
    }

    private Map<String, Object> mapPsychologistToResponse(Psychologist psychologist){
        Map<String, Object> response = new HashMap<>();

        response.put("id", psychologist.getId());
        response.put("email", psychologist.getEmail());
        response.put("fullName", psychologist.getFullName());
        response.put("licenseNumber", psychologist.getLicenseNumber());
        response.put("biography", psychologist.getBiography());
        response.put("yearsExperience", psychologist.getYearsExperience());
        response.put("hourlyRate", psychologist.getHourlyRate());
        response.put("specializations", psychologist.getSpecializations());
        response.put("offersOnline", psychologist.getOffersOnlineSession());
        response.put("offersInPerson", psychologist.getOffersInPersonSession());
        response.put("Locations", psychologist.getOperatingLocations().stream().map(location -> location.getCity()).toList());
        response.put("isActive", psychologist.getIsActive());

        return response;

    }

}
