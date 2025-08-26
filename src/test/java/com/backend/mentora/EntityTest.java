package com.backend.mentora;

import com.backend.mentora.entity.*;
import com.backend.mentora.entity.enums.*;
import com.backend.mentora.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@ActiveProfiles("test")
public class EntityTest {
	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private PsychologistRepository psychologistRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Test
	void testCreateClient() {
		// Given
		Client client = new Client("test@email.com", "password", "Mario", "Rossi", 25);

		// When
		Client savedClient = clientRepository.save(client);

		// Then
		assertThat(savedClient.getId()).isNotNull();
		assertThat(savedClient.getEmail()).isEqualTo("test@email.com");
		assertThat(savedClient.getRole()).isEqualTo(UserRole.CLIENT);
		assertThat(savedClient.getAge()).isEqualTo(25);
		assertThat(savedClient.isClient()).isTrue();
		assertThat(savedClient.isPsychologist()).isFalse();

		System.out.println("✅ Client creato: " + savedClient.getFullName());
	}

	@Test
	void testCreatePsychologist() {
		// Given
		Psychologist psychologist = new Psychologist(
						"psy@email.com",
						"hashedPassword",
						"Laura",
						"Bianchi",
						"PSI123"
		);
		psychologist.setBiography("Specialista in terapia cognitiva");
		psychologist.setYearsExperience(10);
		psychologist.setHourlyRate(new BigDecimal("90.00"));
		psychologist.getSpecializations().add(PsychologistSpecialization.ADULT_PSYCHOLOGY);

		// When
		Psychologist saved = psychologistRepository.save(psychologist);

		// Then
		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getLicenseNumber()).isEqualTo("PSI123");
		assertThat(saved.getSpecializations()).contains(PsychologistSpecialization.ADULT_PSYCHOLOGY);
		assertThat(saved.isPsychologist()).isTrue();

		System.out.println("✅ Psicologo creato: " + saved.getFullName() + " - " + saved.getLicenseNumber());
	}

	@Test
	void testPsychologistSpecializationByAge() {
		// Test calcolo specializzazione per età
		assertThat(PsychologistSpecialization.getByAge(8))
						.isEqualTo(PsychologistSpecialization.CHILD_PSYCHOLOGY);

		assertThat(PsychologistSpecialization.getByAge(15))
						.isEqualTo(PsychologistSpecialization.ADOLESCENT_PSYCHOLOGY);

		assertThat(PsychologistSpecialization.getByAge(30))
						.isEqualTo(PsychologistSpecialization.ADULT_PSYCHOLOGY);

		assertThat(PsychologistSpecialization.getByAge(70))
						.isEqualTo(PsychologistSpecialization.GERIATRIC_PSYCHOLOGY);

		System.out.println("✅ Calcolo specializzazioni per età funziona");
	}

	@Test
	void testQuestionnaireResponse() {
		// Given - Crea cliente
		Client client = new Client("cliente@test.com", "pass", "Anna", "Verdi", 28);
		client = clientRepository.save(client);

		// Given - Crea questionario
		QuestionnaireResponse questionnaire = new QuestionnaireResponse();
		questionnaire.setClient(client);
		questionnaire.setMainReasons("[\"Difficoltà emotive\", \"Ansia legata al futuro\"]");
		questionnaire.setEmotionalDurationMonths(3);
		questionnaire.setShowsDepressiveSymptoms(true);
		questionnaire.setDepressiveSymptomsMonths(2);
		questionnaire.setImpactLevel(3);

		// When
		entityManager.persistAndFlush(questionnaire);

		// Then
		assertThat(questionnaire.getCalculatedPriority()).isEqualTo(Priority.MODERATE);
		assertThat(questionnaire.getRequiredSpecialization()).isEqualTo(PsychologistSpecialization.ADULT_PSYCHOLOGY);
		assertThat(questionnaire.getMainReasonsAsList()).hasSize(2);

		System.out.println("✅ Questionario: " + questionnaire.getCalculatedPriority() +
						" - " + questionnaire.getRequiredSpecialization());
	}

	@Test
	void testClientPsychologistRelation() {
		// Given - Crea client e psicologo
		Client client = new Client("client@test.com", "pass", "Mario", "Rossi", 30);
		Psychologist psychologist = new Psychologist("psy@test.com", "pass", "Dr.", "Smith", "PSI456");

		client = clientRepository.save(client);
		psychologist = psychologistRepository.save(psychologist);

		// Given - Crea relazione
		ClientPsychologistRelation relation = new ClientPsychologistRelation();
		relation.setClient(client);
		relation.setPsychologist(psychologist);
		relation.setClientMessage("Vorrei iniziare un percorso di terapia");

		// When
		entityManager.persistAndFlush(relation);

		// Then
		assertThat(relation.getId()).isNotNull();
		assertThat(relation.getStatus()).isEqualTo(RequestStatus.PENDING);
		assertThat(relation.isPending()).isTrue();
		assertThat(relation.getRequestedAt()).isNotNull();

		// Test accettazione
		relation.accept("Benvenuto nel percorso di terapia");
		assertThat(relation.isAccepted()).isTrue();
		assertThat(relation.canCommunicate()).isTrue();

		System.out.println("✅ Relazione creata: " + relation.getClientFullName() +
						" → " + relation.getPsychologistFullName());
	}

	@Test
	void testLocationAndSearch() {
		// Given
		Location milano = new Location("Milano", "Lombardia", "Italia");
		Location roma = new Location("Roma", "Lazio", "Italia");

		locationRepository.save(milano);
		locationRepository.save(roma);

		// When
		Optional<Location> found = locationRepository.findByCityAndRegion("Milano", "Lombardia");

		// Then
		assertThat(found).isPresent();
		assertThat(found.get().getCity()).isEqualTo("Milano");

		System.out.println("✅ Location trovata: " + found.get().getCity());
	}

	@Test
	void testUserSecurity() {
		// Test che UserDetails funzioni
		Client client = new Client("security@test.com", "password123", "Test", "User", 25);
		client = clientRepository.save(client);

		// Test Spring Security integration
		assertThat(client.getUsername()).isEqualTo("security@test.com");
		assertThat(client.getAuthorities()).hasSize(1);
		assertThat(client.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_CLIENT");
		assertThat(client.isEnabled()).isTrue();

		System.out.println("✅ Security integration funziona per: " + client.getUsername());
	}

	@Test
	void testPasswordEncoding() {
		// Test che le password vengano hashate correttamente
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String plainPassword = "myPassword123";
		String encodedPassword = encoder.encode(plainPassword);

		assertTrue(encoder.matches(plainPassword, encodedPassword));
		assertNotEquals(plainPassword, encodedPassword);

		System.out.println("✅ Password encoding funziona");
	}
}
