package com.backend.mentora.repository;

import com.backend.mentora.entity.Appointment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
	@Query("""
			SELECT a FROM Appointment a
			WHERE a.client.email = :email OR a.psychologist.email = :email
			ORDER BY a.appointmentDateTime DESC
""")
	List<Appointment> findByUserEmailOrderByAppointmentDateTimeDesc(
					@Param("email") String email, Pageable pageable
	);

}
