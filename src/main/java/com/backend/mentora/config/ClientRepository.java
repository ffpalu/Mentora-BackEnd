package com.backend.mentora.config;

import com.backend.mentora.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
	Optional<Client> findByEmail(String email);
}
