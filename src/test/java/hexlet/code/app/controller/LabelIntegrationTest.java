package hexlet.code.app.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import hexlet.code.app.repository.LabelRepository;
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
class LabelIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private LabelRepository labelRepository;

    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        adminToken =
                mockMvc.perform(
                                post("/api/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                """
                                                {
                                                    "username": "hexlet@example.com",
                                                    "password": "qwerty"
                                                }
                                                """))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
    }

    private Long createTestLabel() throws Exception {
        mockMvc.perform(
                        post("/api/labels")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                            "name": "integration-label"
                                        }
                                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("integration-label"))
                .andExpect(jsonPath("$.createdAt").exists());

        return labelRepository.findAll().stream()
                .filter(label -> "integration-label".equals(label.getName()))
                .findFirst()
                .orElseThrow()
                .getId();
    }

    @Test
    void listLabels() throws Exception {
        mockMvc.perform(get("/api/labels").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void createAndShowLabel() throws Exception {
        Long id = createTestLabel();

        mockMvc.perform(get("/api/labels/{id}", id).header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("integration-label"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void updateLabel() throws Exception {
        Long id = createTestLabel();

        mockMvc.perform(
                        put("/api/labels/{id}", id)
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                            "name": "updated-label"
                                        }
                                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updated-label"));

        mockMvc.perform(get("/api/labels/{id}", id).header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updated-label"));
    }

    @Test
    void deleteLabel() throws Exception {
        Long id = createTestLabel();

        mockMvc.perform(
                        delete("/api/labels/{id}", id)
                                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/labels/{id}", id).header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectBlankLabelName() throws Exception {
        mockMvc.perform(
                        post("/api/labels")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                            "name": ""
                                        }
                                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectShortLabelName() throws Exception {
        mockMvc.perform(
                        post("/api/labels")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                            "name": "ab"
                                        }
                                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectDuplicateLabelName() throws Exception {
        mockMvc.perform(
                        post("/api/labels")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                            "name": "feature"
                                        }
                                        """))
                .andExpect(status().isConflict());
    }

    @Test
    void nonexistentLabelReturnsNotFound() throws Exception {
        mockMvc.perform(
                        get("/api/labels/{id}", Long.MAX_VALUE)
                                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
