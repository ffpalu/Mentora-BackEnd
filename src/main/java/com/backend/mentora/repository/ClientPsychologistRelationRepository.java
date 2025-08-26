package com.backend.mentora.repository;

import com.backend.mentora.entity.ClientPsychologistRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientPsychologistRelationRepository extends JpaRepository<ClientPsychologistRelation, Long> {
}
