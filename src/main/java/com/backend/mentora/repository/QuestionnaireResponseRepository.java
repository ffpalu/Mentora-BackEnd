package com.backend.mentora.repository;

import com.backend.mentora.entity.Client;
import com.backend.mentora.entity.QuestionnaireResponse;
import com.backend.mentora.entity.enums.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionnaireResponseRepository extends JpaRepository<QuestionnaireResponse, Long> {

    Optional<QuestionnaireResponse> findByClient(Client client);
    List<QuestionnaireResponse> findByCalculatedPriority(Priority priority);
    boolean existsByClient(Client client);

}
