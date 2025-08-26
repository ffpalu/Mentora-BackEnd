package com.backend.mentora.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {



	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
						.cors(cors -> cors.configurationSource(corsConfigurationSource()))
						.csrf(csrf -> csrf.disable())
						.authorizeHttpRequests( auth -> auth
										.requestMatchers("/h2-console/**").permitAll()
										.requestMatchers("/api/auth/**").permitAll()
										.requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
										.anyRequest().permitAll()
						)
						.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())
						);
		return http.build();
	}


 	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of(
						"http://localhost:3000",
						"http://localhost:5173",
						"http://localhost:8080"
		));

		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/api/**", configuration);
		return source;
	}
}
