package com.backend.mentora.repository;

import com.backend.mentora.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location,Long> {
	Optional<Location> findByCityAndRegion(String cyty, String region);
	List<Location> findByCity(String city);
}
