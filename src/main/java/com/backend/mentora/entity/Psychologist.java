package com.backend.mentora.entity;

import com.backend.mentora.entity.enums.PsychologistSpecialization;
import com.backend.mentora.entity.enums.RequestStatus;
import com.backend.mentora.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;




@Entity
@Table(name = "psychologists")
@PrimaryKeyJoinColumn(name = "user_id")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Psychologist extends User{

    @Column(name = "license_number", unique = true, nullable = false)
    private String licenseNumber;

    @Column(columnDefinition = "TEXT")
    private String biography;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    @Column(name = "hourly_rate")
    private BigDecimal hourlyRate;

    @Column(name = "offers_online_sessions", nullable = false)
    private Boolean offersOnlineSession = true;

    @Column(name = "offers_in_person_sessions", nullable = false)
    private Boolean offersInPersonSession = true;

    @ElementCollection(targetClass = PsychologistSpecialization.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "psychologist_specializations", joinColumns = @JoinColumn(name = "psychologist_id"))
    @Column(name = "specialization")
    private Set<PsychologistSpecialization> specializations = new HashSet<>();

    @ManyToMany(fetch =  FetchType.LAZY)
    @JoinTable(name = "psychologist_locations", joinColumns = @JoinColumn(name = "psychologist_id"), inverseJoinColumns = @JoinColumn(name = "location_id"))
    private  Set<Location> operatingLocations = new HashSet<>();

    @OneToMany(mappedBy = "psychologist", fetch = FetchType.LAZY)
    private List<ClientPsychologistRelation> clientRelations = new ArrayList<>();

    public Psychologist(String email, String password, String firstName, String lastName, String LicenseNumber) {
        this.setEmail(email);
        this.setPassword(password);;
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setRole(UserRole.PSYCHOLOGIST);
        this.setIsActive(true);
        this.licenseNumber = LicenseNumber;
    }

    public boolean canWorkWith(PsychologistSpecialization specialization) {
        return specializations.contains(specialization);
    }

    public boolean operatesInCity(String city) {
        return operatingLocations.stream()
                .anyMatch(location -> location.getCity().equals(city));
    }


    public List<ClientPsychologistRelation> getPendingRequests() {
        return clientRelations.stream()
                .filter(relation -> relation.getStatus() == RequestStatus.PENDING)
                .toList();
    }


}
