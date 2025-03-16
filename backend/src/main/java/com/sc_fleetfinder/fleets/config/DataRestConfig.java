package com.sc_fleetfinder.fleets.config;

import com.sc_fleetfinder.fleets.entities.GameEnvironment;
import com.sc_fleetfinder.fleets.entities.GameExperience;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;
import com.sc_fleetfinder.fleets.entities.GroupStatus;
import com.sc_fleetfinder.fleets.entities.Legality;
import com.sc_fleetfinder.fleets.entities.PlanetMoonSystem;
import com.sc_fleetfinder.fleets.entities.PlanetarySystem;
import com.sc_fleetfinder.fleets.entities.PlayStyle;
import com.sc_fleetfinder.fleets.entities.PvpStatus;
import com.sc_fleetfinder.fleets.entities.ServerRegion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;


@Configuration
public class DataRestConfig implements RepositoryRestConfigurer {

    @Value("${allowed.origins}")
    private String[] theAllowedOrigins;

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
//        RepositoryRestConfigurer.super.configureRepositoryRestConfiguration(config, cors);

        HttpMethod[] unsupportedMethods = { HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.PATCH };

        cors.addMapping(config.getBasePath() + "/**").allowedOrigins(theAllowedOrigins);
        //User and Group Listing support GET, POST, PUT, DELETE
        //Other entities should be immutable

        //disable HTTP methods for GameEnvironment: POST, PUT, DELETE
        config.getExposureConfiguration()
                .forDomainType(GameEnvironment.class)
                .withItemExposure((metdata, httpMethods) -> httpMethods.disable(unsupportedMethods))
                .withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(unsupportedMethods));

        //disable HTTP methods for GameExperience: POST, PUT, DELETE
        config.getExposureConfiguration()
                .forDomainType(GameExperience.class)
                .withItemExposure((metdata, httpMethods) -> httpMethods.disable(unsupportedMethods))
                .withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(unsupportedMethods));

        //disable HTTP methods for GameplayCategory: POST, PUT, DELETE
        config.getExposureConfiguration()
                .forDomainType(GameplayCategory.class)
                .withItemExposure((metdata, httpMethods) -> httpMethods.disable(unsupportedMethods))
                .withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(unsupportedMethods));

        //disable HTTP methods for GameplaySubcategory: POST, PUT, DELETE
        config.getExposureConfiguration()
                .forDomainType(GameplaySubcategory.class)
                .withItemExposure((metdata, httpMethods) -> httpMethods.disable(unsupportedMethods))
                .withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(unsupportedMethods));


        //disable http methods for GroupStatus: POST, PUT, DELETE
        config.getExposureConfiguration()
                .forDomainType(GroupStatus.class)
                .withItemExposure((metadata, httpMethods) -> httpMethods.disable(unsupportedMethods))
                .withCollectionExposure((metadata, httpMethods) -> httpMethods.disable(unsupportedMethods));

        //disable HTTP methods for Legality: POST, PUT, DELETE
        config.getExposureConfiguration()
                .forDomainType(Legality.class)
                .withItemExposure((metadata, httpMethods) -> httpMethods.disable(unsupportedMethods))
                .withCollectionExposure((metadata, httpMethods) -> httpMethods.disable(unsupportedMethods));

        //disable HTTP methods for PlanetarySystem: POST, PUT, DELETE
        config.getExposureConfiguration()
                .forDomainType(PlanetarySystem.class)
                .withItemExposure((metdata, httpMethods) -> httpMethods.disable(unsupportedMethods))
                .withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(unsupportedMethods));

        //disable HTTP methods for PlanetMoonSystem: POST, PUT, DELETE
        config.getExposureConfiguration()
                .forDomainType(PlanetMoonSystem.class)
                .withItemExposure((metdata, httpMethods) -> httpMethods.disable(unsupportedMethods))
                .withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(unsupportedMethods));

        //disable HTTP methods for PlayStyle: POST, PUT, DELETE
        config.getExposureConfiguration()
                .forDomainType(PlayStyle.class)
                .withItemExposure((metdata, httpMethods) -> httpMethods.disable(unsupportedMethods))
                .withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(unsupportedMethods));

        //disable HTTP methods for PvpStatus: POST, PUT, DELETE
        config.getExposureConfiguration()
                .forDomainType(PvpStatus.class)
                .withItemExposure((metadata, httpMethods) -> httpMethods.disable(unsupportedMethods))
                .withCollectionExposure((metadata, httpMethods) -> httpMethods.disable(unsupportedMethods));

        //disable HTTP methods for ServerRegion: POST, PUT, DELETE
        config.getExposureConfiguration()
                .forDomainType(ServerRegion.class)
                .withItemExposure((metdata, httpMethods) -> httpMethods.disable(unsupportedMethods))
                .withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(unsupportedMethods));
    }
}
