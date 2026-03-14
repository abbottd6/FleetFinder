package com.sc_fleetfinder.fleets.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("!test")
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        var defaultConverter = new JwtGrantedAuthoritiesConverter();

        Converter<Jwt, ? extends AbstractAuthenticationToken> keycloakJwtAuthConverter = jwt -> {
            // default SCOPE authorities
            Collection<GrantedAuthority> authorities = new ArrayList<>(defaultConverter.convert(jwt));

            // realm roles and custom roles from keycloak
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> roles) {
                roles.forEach(r -> authorities.add(new SimpleGrantedAuthority("ROLE_" + r)));
            }

            return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
        };

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/websocket/**").permitAll()
                        .requestMatchers("/api/users/me").authenticated()
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/group-listings/create_listing").authenticated()
                        .requestMatchers("/api/group-listings").permitAll()
                        .requestMatchers("/api/modctrl/**").hasRole("mod")
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(j -> j.jwtAuthenticationConverter(keycloakJwtAuthConverter)));
        return http.build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

//    @Bean
//    OncePerRequestFilter logAuthorities() {
//        return new OncePerRequestFilter() {
//            @Override
//            protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
//                    throws ServletException, IOException {
//                var auth = SecurityContextHolder.getContext().getAuthentication();
//                if (auth != null) {
//                    System.out.println("User=" + auth.getName() + " authorities=" + auth.getAuthorities());
//                }
//                chain.doFilter(req, res);
//            }
//        };
//    }
}
