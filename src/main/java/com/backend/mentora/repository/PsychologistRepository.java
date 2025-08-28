package com.backend.mentora.repository;

import com.backend.mentora.entity.Psychologist;
import com.backend.mentora.entity.enums.PsychologistSpecialization;
import com.backend.mentora.entity.enums.SessionMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PsychologistRepository extends JpaRepository<Psychologist, Long> {
	Optional<Psychologist> findByEmail(String email);
	Optional<Psychologist> findByLicenseNumber(String licenseNumber);


	@Query("""
    SELECT DISTINCT p FROM Psychologist p 
    JOIN p.specializations s 
    WHERE s = :specialization 
    AND p.isActive = true
    AND (CAST(:sessionMode AS string) = 'INDIFFERENT' 
         OR (CAST(:sessionMode AS string) = 'ONLINE' AND p.offersOnlineSessions = true)
         OR (CAST(:sessionMode AS string) IN ('IN_PERSON', 'MIXED') AND p.offersInPersonSessions = true))
    """)
	List<Psychologist> findBySpecializationAndSessionMode(
			@Param("specialization") PsychologistSpecialization specialization,
			@Param("sessionMode") SessionMode sessionMode
	);


	@Query("""
    SELECT DISTINCT p FROM Psychologist p
    JOIN p.specializations s
    WHERE s =:specialization AND p.isActive = true
    """
	)
	List<Psychologist> findBySpecialization(
			@Param("specialization") PsychologistSpecialization specialization
	);


	@Query("""
	SELECT DISTINCT p FROM Psychologist p
	JOIN p.specializations s
	JOIN p.operatingLocations l
	WHERE l.city = :city
	AND s = :specialization
	AND p.isActive = true
	AND (CAST(:sessionMode AS string) = 'INDIFFERENT'
		OR(CAST(:sessionMode AS string) = 'ONLINE' AND p.offersOnlineSessions = true )
		OR(CAST(:sessionMode AS string) = 'IN_PERSON' AND p.offersInPersonSessions = true)
		OR(CAST(:sessionMode AS string) = 'MIXED' AND  p.offersOnlineSessions = true  AND  p.offersInPersonSessions = true))
	""")
	List<Psychologist> findSuitablePsychologists(
			@Param("city") String city,
			@Param("specialization") PsychologistSpecialization specialization,
			@Param("sessionMode") SessionMode sessionMode
	);
}
