package com.sc_fleetfinder.fleets.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Map;
import java.util.stream.Collectors;

public class TestEnvironmentLoader implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        Dotenv dotenv = Dotenv.configure()
                .filename("backend/.env")
                .ignoreIfMissing()
                .load();

        Map<String, Object> envMap = dotenv.entries().stream()
                .collect(Collectors.toMap(
                        DotenvEntry::getKey,
                        DotenvEntry::getValue
                ));

        PropertySource<Map<String, Object>> propertySource = new MapPropertySource("dotenv", envMap);
        context.getEnvironment().getPropertySources().addFirst(propertySource);

        System.out.println("Loaded DB_USERNAME: " + dotenv.get("DB_USERNAME"));
        System.out.println("Working Directory: " + System.getProperty("user.dir"));
    }
}
