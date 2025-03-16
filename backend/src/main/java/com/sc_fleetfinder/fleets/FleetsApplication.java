package com.sc_fleetfinder.fleets;

import com.sc_fleetfinder.fleets.config.TestEnvironmentLoader;
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
		app.addInitializers(new TestEnvironmentLoader());
		app.run(args);
	}

}
