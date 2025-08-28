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
        WHERE s =:specialization
        AND p.isActive = true 
        AND (:sessionMode = 'INDIFFERENT'
                OR (:sessionMode = 'ONLINE' AND p.offersOnlineSession = true )
                OR (:sessionMode IN ('IN_PERSON', 'MIXED') AND p.offersInPersonSession = true ))
        """
	)
	List<Psychologist> findBySpecializationAndSessionMode(
			@Param("specialization") PsychologistSpecialization specialization,
			@Param("sessionMode") SessionMode sessionMode
	);


	@Query("""
    SELECT DISTINCT p FROM Psychologist p
    JOIN p.specializations s
    WHERE S =:specialization AND p.isActive = true
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
	AND (:sessionMode = 'INDIFFERENT'
		OR(:sessionMode = 'ONLINE' AND p.offersOnlineSession = true )
		OR(:sessionMode = 'IN_PERSON' AND p.offersInPersonSession = true)
		OR(:sessionMode = 'MIXED' AND  p.offersOnlineSession = true  AND  p.offersInPersonSession = true))
	""")
	List<Psychologist> findSuitablePsychologists(
			@Param("city") String city,
			@Param("specialization") PsychologistSpecialization specialization,
			@Param("sessionMode") SessionMode sessionMode
	);
}
