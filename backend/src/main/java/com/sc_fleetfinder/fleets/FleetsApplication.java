package com.sc_fleetfinder.fleets;

import com.sc_fleetfinder.fleets.config.TestEnvironmentLoader;
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching
public class FleetsApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(FleetsApplication.class);

		//determining spring profile with environment variable
		String profile = System.getenv("SPRING_PROFILE");
		if (profile == null || profile.isBlank()) {
			profile = "dev";
		}

		System.setProperty("spring.profiles.active", profile);

		app.addInitializers(new TestEnvironmentLoader());
		app.run(args);
	}

	@PostConstruct
	public void logActiveProfile() {
		String activeProfile = System.getProperty("spring.profiles.active", "default");
		System.out.println("Spring Active Profile: " + activeProfile);
	}
}
