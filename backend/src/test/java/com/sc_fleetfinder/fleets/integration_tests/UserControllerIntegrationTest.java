package com.sc_fleetfinder.fleets.integration_tests;

import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.config.TestEnvironmentLoader;
import com.sc_fleetfinder.fleets.entities.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@ContextConfiguration(initializers = TestEnvironmentLoader.class)
public class UserControllerIntegrationTest extends AbstractIntegrationTestDB {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private static final String MOCK_KCID = "someKeycloakId";
    private static final String MOCK_USERNAME = "TestUser";
    private static final String MOCK_EMAIL = "test@gmail.com";

    @BeforeEach
    void verifyTestUser() {
        userRepository.findByKeycloakId(MOCK_KCID)
                .orElseGet(() -> {
                    Users mockUser = new Users();
                    mockUser.setKeycloakId(MOCK_KCID);
                    mockUser.setEmail(MOCK_EMAIL);
                    mockUser.setUsername(MOCK_USERNAME);
                    mockUser.setIsDeleted(false);

                    return userRepository.save(mockUser);
                });
    }

    @Test
    void testGetUsers_Success() throws Exception {
        mockMvc.perform(get("/api/users")
                        .with(jwt()
                                .jwt(j -> j.subject(MOCK_KCID))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId").isNumber())
                .andExpect(jsonPath("$[0].username").value(MOCK_USERNAME))
                .andExpect(jsonPath("$[0].server").exists())
                .andExpect(jsonPath("$[0].org").exists())
                .andExpect(jsonPath("$[0].about").exists())
                .andExpect(jsonPath("$[0].email").doesNotExist())
                .andExpect(jsonPath("$[0].groupListingsDto").doesNotExist())
                .andExpect(jsonPath("$[0].acctCreated").doesNotExist());
    }

    @Test
    void testGetUsers_NotAuthorized() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUserById_Success() throws Exception {
        mockMvc.perform(get("/api/users/1")
                .with(jwt()
                        .jwt(j -> j.subject(MOCK_KCID))
                        .authorities(new SimpleGrantedAuthority("ROLE_user")))
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").isNumber())
                .andExpect(jsonPath("$.username").value(MOCK_USERNAME))
                .andExpect(jsonPath("$.server").exists())
                .andExpect(jsonPath("$.org").exists())
                .andExpect(jsonPath("$.about").exists())
                .andExpect(jsonPath("$.email").doesNotExist())
                .andExpect(jsonPath("$.groupListingsDto").doesNotExist())
                .andExpect(jsonPath("$.acctCreated").doesNotExist());
    }

    @Test
    void testGetUserById_Failure() throws Exception {
        mockMvc.perform(get("/api/users/350")
                .with(jwt()
                        .jwt(j -> j.subject(MOCK_KCID))
                        .authorities(new SimpleGrantedAuthority("ROLE_user")))
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUsersById_Failure_NoAuth() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetMe_Found() throws Exception {
        mockMvc.perform(get("/api/users/me")
                .with(jwt()
                        .jwt(j -> j.subject(MOCK_KCID))
                        .authorities(new SimpleGrantedAuthority("ROLE_user")))
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").isNumber())
                .andExpect(jsonPath("$.username").value(MOCK_USERNAME))
                .andExpect(jsonPath("$.server").exists())
                .andExpect(jsonPath("$.org").exists())
                .andExpect(jsonPath("$.about").exists())
                .andExpect(jsonPath("$.acctCreated").exists())
                .andExpect(jsonPath("$.groupListingsDto").exists());
    }

    @Test
    void testGetMe_NotFound() throws Exception {
        mockMvc.perform(get("/api/users/me")
                .with(jwt().jwt(j -> j.subject("doesNotExist")))
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetMe_NotAuthorized() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateUser_SuccessFromMe404() throws Exception {
        mockMvc.perform(post("/api/users/create-user")
                .with(jwt().jwt(j -> j
                        .subject("newUserKeycloakId")
                        .claim("preferred_username", "newUser")
                        .claim("email", "newuser@gmail.com")
                )
                        .authorities(new SimpleGrantedAuthority("ROLE_user")))
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").isNumber())
                .andExpect(jsonPath("$.username").value("newUser"));
    }

    @Test
    void testCreateUser_FailExistingKeycloak() throws Exception {
        mockMvc.perform(post("/api/users/create-user")
                .with(jwt()
                        .jwt(j -> j.subject(MOCK_KCID))
                        .authorities(new SimpleGrantedAuthority("ROLE_user")))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void testCreateUser_FailNoAuth() throws Exception {
        mockMvc.perform(post("/api/users/create-user")
                .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }
}
