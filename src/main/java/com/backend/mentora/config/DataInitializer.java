package com.backend.mentora.config;

import com.backend.mentora.entity.Client;
import com.backend.mentora.entity.Location;
import com.backend.mentora.entity.Psychologist;
import com.backend.mentora.entity.enums.PsychologistSpecialization;
import com.backend.mentora.entity.enums.SessionMode;
import com.backend.mentora.repository.ClientRepository;
import com.backend.mentora.repository.LocationRepository;
import com.backend.mentora.repository.PsychologistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
	private final LocationRepository locationRepository;
	private final ClientRepository clientRepository;
	private final PsychologistRepository psychologistRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {
		// Controlla se i dati esistono già
		if (locationRepository.count() > 0) {
			System.out.println("📊 Dati già presenti nel database");
			return;
		}

		System.out.println("🚀 Inizializzazione dati di test...");

		// ===============================
		// CREA LOCATIONS
		// ===============================
		Location milano = new Location("Milano", "Lombardia", "Italia");
		Location roma = new Location("Roma", "Lazio", "Italia");
		Location torino = new Location("Torino", "Piemonte", "Italia");

		locationRepository.saveAll(List.of(milano, roma, torino));
		System.out.println("📍 Locations create: Milano, Roma, Torino");

		// ===============================
		// CREA PSICOLOGI DI TEST
		// ===============================

		// Dr. Marco Rossi - Psicologo per adulti
		Psychologist drRossi = new Psychologist(
						"dr.rossi@mentora.com",
						passwordEncoder.encode("password123"),
						"Marco",
						"Rossi",
						"PSI001MI"
		);
		drRossi.setBiography("Specialista in terapia cognitivo-comportamentale per adulti. " +
						"Esperienza nel trattamento di ansia, depressione e disturbi dell'umore.");
		drRossi.setYearsExperience(8);
		drRossi.setHourlyRate(new BigDecimal("80.00"));
		drRossi.setOffersOnlineSessions(true);
		drRossi.setOffersInPersonSessions(true);
		drRossi.getSpecializations().add(PsychologistSpecialization.ADULT_PSYCHOLOGY);
		drRossi.getOperatingLocations().add(milano);
		drRossi.getOperatingLocations().add(roma);

		// Dr.ssa Laura Bianchi - Psicologa età evolutiva
		Psychologist drBianchi = new Psychologist(
						"dr.bianchi@mentora.com",
						passwordEncoder.encode("password123"),
						"Laura",
						"Bianchi",
						"PSI002MI"
		);
		drBianchi.setBiography("Psicologa dell'età evolutiva specializzata in disturbi dell'apprendimento " +
						"e supporto psicologico per bambini e adolescenti.");
		drBianchi.setYearsExperience(12);
		drBianchi.setHourlyRate(new BigDecimal("90.00"));
		drBianchi.setOffersOnlineSessions(true);
		drBianchi.getSpecializations().add(PsychologistSpecialization.CHILD_PSYCHOLOGY);
		drBianchi.getSpecializations().add(PsychologistSpecialization.ADOLESCENT_PSYCHOLOGY);
		drBianchi.getOperatingLocations().add(milano);

		// Dr. Giuseppe Verdi - Specialista dipendenze
		Psychologist drVerdi = new Psychologist(
						"dr.verdi@mentora.com",
						passwordEncoder.encode("password123"),
						"Giuseppe",
						"Verdi",
						"PSI003TO"
		);
		drVerdi.setBiography("Psicoterapeuta specializzato nel trattamento delle dipendenze " +
						"e nel supporto per traumi e disturbi post-traumatici.");
		drVerdi.setYearsExperience(15);
		drVerdi.setHourlyRate(new BigDecimal("100.00"));
		drVerdi.setOffersOnlineSessions(false); // Solo in presenza
		drVerdi.getSpecializations().add(PsychologistSpecialization.ADULT_PSYCHOLOGY);
		drVerdi.getOperatingLocations().add(torino);

		psychologistRepository.saveAll(List.of(drRossi, drBianchi, drVerdi));
		System.out.println("👨‍⚕️ Psicologi creati: Dr. Rossi, Dr.ssa Bianchi, Dr. Verdi");

		// ===============================
		// CREA CLIENTI DI TEST
		// ===============================

		// Anna Neri - Cliente Milano
		Client anna = new Client(
						"anna.neri@email.com",
						passwordEncoder.encode("password123"),
						"Anna",
						"Neri",
						28
		);
		anna.setLocation(milano);
		anna.setPreferredSessionMode(SessionMode.MIXED);
		anna.setPhoneNumber("+39 333 1234567");

		// Luca Blu - Cliente giovane
		Client luca = new Client(
						"luca.blu@email.com",
						passwordEncoder.encode("password123"),
						"Luca",
						"Blu",
						16
		);
		luca.setLocation(milano);
		luca.setPreferredSessionMode(SessionMode.ONLINE);
		luca.setPhoneNumber("+39 333 7654321");

		// Maria Verdi - Cliente Roma
		Client maria = new Client(
						"maria.verdi@email.com",
						passwordEncoder.encode("password123"),
						"Maria",
						"Verdi",
						45
		);
		maria.setLocation(roma);
		maria.setPreferredSessionMode(SessionMode.IN_PERSON);

		clientRepository.saveAll(List.of(anna, luca, maria));
		System.out.println("👥 Clienti creati: Anna (28), Luca (16), Maria (45)");

		// ===============================
		// SUMMARY
		// ===============================
		System.out.println("\n" + "=".repeat(50));
		System.out.println("✅ INIZIALIZZAZIONE COMPLETATA!");
		System.out.println("=".repeat(50));
		System.out.println("🏥 3 Locations: Milano, Roma, Torino");
		System.out.println("👨‍⚕️3 Psicologi con specializzazioni diverse");
		System.out.println("👥 3 Clienti di età diverse");
		System.out.println();
		System.out.println("🔑 CREDENZIALI DI TEST:");
		System.out.println("   Psicologi:");
		System.out.println("   - dr.rossi@mentora.com / password123");
		System.out.println("   - dr.bianchi@mentora.com / password123");
		System.out.println("   - dr.verdi@mentora.com / password123");
		System.out.println();
		System.out.println("   Clienti:");
		System.out.println("   - anna.neri@email.com / password123");
		System.out.println("   - luca.blu@email.com / password123");
		System.out.println("   - maria.verdi@email.com / password123");
		System.out.println();
		System.out.println("🌐 H2 Console: http://localhost:8080/h2-console");
		System.out.println("📚 API Docs: http://localhost:8080/swagger-ui.html");
		System.out.println("=".repeat(50));
	}
}
