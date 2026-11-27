package hexlet.code.app.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import hexlet.code.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private UserRepository userRepository;

    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        adminToken = login("hexlet@example.com", "qwerty");
    }

    private String login(String email, String password) throws Exception {
        return mockMvc.perform(
                        post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                            "username": "%s",
                                            "password": "%s"
                                        }
                                        """
                                                .formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    private Long createTestUser() throws Exception {
        mockMvc.perform(
                        post("/api/users")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                            "firstName": "Integration",
                                            "lastName": "Test",
                                            "email": "integration@example.com",
                                            "password": "test12345",
                                            "admin": true
                                        }
                                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.admin").value(false))
                .andExpect(jsonPath("$.password").doesNotExist());

        return userRepository.findByEmail("integration@example.com").orElseThrow().getId();
    }

    @Test
    void listUsers() throws Exception {
        mockMvc.perform(get("/api/users").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void createAndShowUser() throws Exception {
        Long id = createTestUser();

        mockMvc.perform(get("/api/users/{id}", id).header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Integration"))
                .andExpect(jsonPath("$.email").value("integration@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        login("integration@example.com", "test12345");
    }

    @Test
    void userCanUpdateOwnProfile() throws Exception {
        Long id = createTestUser();
        String token = login("integration@example.com", "test12345");

        mockMvc.perform(
                        put("/api/users/{id}", id)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                            "firstName": "Updated",
                                            "lastName": "User",
                                            "admin": true,
                                            "email": "changed@example.com"
                                        }
                                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.admin").value(false))
                .andExpect(jsonPath("$.email").value("integration@example.com"));
    }

    @Test
    void userCannotUpdateAdministrator() throws Exception {
        createTestUser();

        String token = login("integration@example.com", "test12345");

        Long adminId = userRepository.findByEmail("hexlet@example.com").orElseThrow().getId();

        mockMvc.perform(
                        put("/api/users/{id}", adminId)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                            "firstName": "Unauthorized",
                                            "lastName": "Update"
                                        }
                                        """))
                .andExpect(status().isForbidden());
    }

    @Test
    void userCannotDeleteAdministrator() throws Exception {
        createTestUser();

        String token = login("integration@example.com", "test12345");

        Long adminId = userRepository.findByEmail("hexlet@example.com").orElseThrow().getId();

        mockMvc.perform(
                        delete("/api/users/{id}", adminId)
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void administratorCanDeleteUser() throws Exception {
        Long id = createTestUser();

        mockMvc.perform(
                        delete("/api/users/{id}", id)
                                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/{id}", id).header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void nonexistentUserReturnsNotFound() throws Exception {
        mockMvc.perform(
                        get("/api/users/{id}", Long.MAX_VALUE)
                                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
