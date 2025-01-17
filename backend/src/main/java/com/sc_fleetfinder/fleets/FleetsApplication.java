package com.sc_fleetfinder.fleets;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching
public class FleetsApplication {

	public static void main(String[] args) {
		SpringApplication.run(FleetsApplication.class, args);
	}

}
