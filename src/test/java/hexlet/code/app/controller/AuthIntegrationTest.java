package hexlet.code.app.controller;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginWithValidCredentials() throws Exception {

        String token = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "hexlet@example.com",
                                    "password": "qwerty"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(token.startsWith("eyJ"));

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void loginWithInvalidPassword() throws Exception {

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "hexlet@example.com",
                                    "password": "wrongpassword"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithUnknownUser() throws Exception {

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "unknown@example.com",
                                    "password": "qwerty"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointWithoutToken() throws Exception {

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }
}