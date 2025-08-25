package com.backend.mentora.config;

import com.backend.mentora.entity.Psychologist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PsychologistRepository extends JpaRepository<Psychologist, Long> {
	Optional<Psychologist> findByEmail(String email);
	Optional<Psychologist> findByLicenseNumber(String licenseNumber);
}
