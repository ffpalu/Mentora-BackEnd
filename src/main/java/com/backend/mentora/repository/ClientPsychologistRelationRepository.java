package com.backend.mentora.repository;

import com.backend.mentora.entity.Client;
import com.backend.mentora.entity.ClientPsychologistRelation;
import com.backend.mentora.entity.Psychologist;
import com.backend.mentora.entity.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientPsychologistRelationRepository extends JpaRepository<ClientPsychologistRelation, Long> {
	List<ClientPsychologistRelation> findByClientAndIsActive(Client client, Boolean isActive);
	List<ClientPsychologistRelation> findByPsychologistAndStatus(Psychologist psychologist, RequestStatus status);
}
