package com.backend.mentora;

import com.backend.mentora.entity.*;
import com.backend.mentora.entity.enums.*;
import com.backend.mentora.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class RepositoryTest {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private PsychologistRepository psychologistRepository;

	@Test
	void testFindByEmail() {
		// Given
		Client client = new Client("find@test.com", "pass", "Find", "Me", 25);
		clientRepository.save(client);

		// When
		Optional<User> foundUser = userRepository.findByEmail("find@test.com");
		Optional<Client> foundClient = clientRepository.findByEmail("find@test.com");

		// Then
		assertThat(foundUser).isPresent();
		assertThat(foundClient).isPresent();
		assertThat(foundUser.get().getEmail()).isEqualTo("find@test.com");

		System.out.println("✅ Repository findByEmail funziona");
	}

	@Test
	void testEmailUniqueness() {
		// Given
		Client client1 = new Client("unique@test.com", "pass1", "User", "One", 25);
		clientRepository.save(client1);

		// When & Then - Tentativo di creare stesso email dovrebbe fallire
		assertThrows(Exception.class, () -> {
			Client client2 = new Client("unique@test.com", "pass2", "User", "Two", 30);
			clientRepository.saveAndFlush(client2);
		});

		System.out.println("✅ Email uniqueness constraint funziona");
	}
}
