package com.sc_fleetfinder.fleets.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

//public class TestEnvironmentLoader implements ApplicationContextInitializer<ConfigurableApplicationContext> {
//
//    @Override
//    public void initialize(ConfigurableApplicationContext context) {
//
//        //Getting the active Spring profile (either 'dev' or 'prod'
//        //prod is set (for prod) in docker-compose.dev.yml/backend section
//        String activeProfile = System.getProperty("spring.profiles.active", "dev");
//        System.out.println("Active Profile: " + activeProfile);
//
//        //concat .env filename
//        String envFileName = ".env." + activeProfile;
//        System.out.println(envFileName);
//
//        //setting environment directory based on activeProfile
//        String envDir = null;
//        if (activeProfile.equals("prod")) {
//            envDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize().toString();
//        }
//        else {
//            envDir = System.getProperty("user.dir");
//        }
//
//        Dotenv dotenv = Dotenv.configure()
//                .directory(envDir)
//                .filename(envFileName)
//                .ignoreIfMissing()
//                .load();
//
//        Map<String, Object> envMap = dotenv.entries().stream()
//                .collect(Collectors.toMap(
//                        DotenvEntry::getKey,
//                        DotenvEntry::getValue
//                ));
//
//        PropertySource<Map<String, Object>> propertySource = new MapPropertySource("dotenv", envMap);
//        context.getEnvironment().getPropertySources().addFirst(propertySource);
//
////        System.out.println("Loaded DB_USERNAME: " + dotenv.get("DB_USERNAME"));
////        System.out.println("Loaded DB_PASSWORD: " + dotenv.get("DB_PASSWORD"));
////        System.out.println("Loaded DB_HOST: " + dotenv.get("DB_HOST"));
////        System.out.println("Loaded DB_PORT: " + dotenv.get("DB_PORT"));
////        System.out.println("Working Directory: " + System.getProperty("user.dir"));
//    }
//}
