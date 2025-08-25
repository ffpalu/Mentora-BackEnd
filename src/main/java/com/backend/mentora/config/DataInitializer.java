package com.backend.mentora.config;

import com.backend.mentora.entity.Client;
import com.backend.mentora.entity.Location;
import com.backend.mentora.entity.Psychologist;
import com.backend.mentora.utils.PsychologistSpecialization;
import com.backend.mentora.utils.SessionMode;
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
		Location milano = new Location("Milano", "Lombardia", "Italia");
		Location roma = new Location("Roma", "Lazio", "Italia");

		locationRepository.saveAll(List.of(milano,roma));


		Psychologist drRossi = new Psychologist(
						"dr.rossi@email.com",
						passwordEncoder.encode("password123"),
						"Marco",
						"Rossi",
						"PSI001MI"
		);

		drRossi.setBiography("Specialista in terapia cognitivo-comportamentale");
		drRossi.setYearsExperience(8);
		drRossi.setHourlyRate(new BigDecimal("80.00"));
		drRossi.setOffersOnlineSession(true);
		drRossi.getSpecializations().add(PsychologistSpecialization.ADULT_PSYCHOLOGY);
		drRossi.getOperatingLocations().add(milano);


		Client cliente1 = new Client(
						"cliente1@email.com",
						passwordEncoder.encode("password123"),
						"Anna",
						"Verdi",
						28
		);

		cliente1.setLocation(milano);
		cliente1.setPreferredSessionMode(SessionMode.MIXED);

		psychologistRepository.save(drRossi);
		clientRepository.save(cliente1);

		System.out.println("✅ Dati di test creati!");
		System.out.println("🔑 Login psicologo: dr.rossi@email.com / password123");
		System.out.println("🔑 Login cliente: cliente1@email.com / password123");
	}
}
