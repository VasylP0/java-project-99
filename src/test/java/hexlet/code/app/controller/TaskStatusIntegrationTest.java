package hexlet.code.app.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import hexlet.code.app.repository.TaskStatusRepository;
import jakarta.persistence.EntityManager;
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
class TaskStatusIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private TaskStatusRepository taskStatusRepository;

    @Autowired private EntityManager entityManager;

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

    private Long createTestStatus() throws Exception {
        mockMvc.perform(
                        post("/api/task_statuses")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                            "name": "Integration Status",
                                            "slug": "integration_status"
                                        }
                                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Integration Status"))
                .andExpect(jsonPath("$.slug").value("integration_status"))
                .andExpect(jsonPath("$.createdAt").exists());

        return taskStatusRepository.findBySlug("integration_status").orElseThrow().getId();
    }

    @Test
    void listTaskStatuses() throws Exception {
        mockMvc.perform(get("/api/task_statuses").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void createAndShowTaskStatus() throws Exception {
        Long id = createTestStatus();

        mockMvc.perform(
                        get("/api/task_statuses/{id}", id)
                                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Integration Status"))
                .andExpect(jsonPath("$.slug").value("integration_status"));
    }

    @Test
    void updateTaskStatus() throws Exception {
        Long id = createTestStatus();

        mockMvc.perform(
                        put("/api/task_statuses/{id}", id)
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                            "name": "Updated Status",
                                            "slug": "updated_status"
                                        }
                                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Status"))
                .andExpect(jsonPath("$.slug").value("updated_status"));

        mockMvc.perform(
                        get("/api/task_statuses/{id}", id)
                                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("updated_status"));
    }

    @Test
    void deleteTaskStatus() throws Exception {
        Long id = createTestStatus();

        mockMvc.perform(
                        delete("/api/task_statuses/{id}", id)
                                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get("/api/task_statuses/{id}", id)
                                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectBlankName() throws Exception {
        mockMvc.perform(
                        post("/api/task_statuses")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                            "name": "",
                                            "slug": "valid_slug"
                                        }
                                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectBlankSlug() throws Exception {
        mockMvc.perform(
                        post("/api/task_statuses")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                            "name": "Valid Name",
                                            "slug": ""
                                        }
                                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectDuplicateName() throws Exception {
        mockMvc.perform(
                        post("/api/task_statuses")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                            "name": "Draft",
                                            "slug": "another_draft"
                                        }
                                        """))
                .andExpect(status().isConflict());
    }

    @Test
    void rejectDuplicateSlug() throws Exception {
        mockMvc.perform(
                        post("/api/task_statuses")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                            "name": "Another Draft",
                                            "slug": "draft"
                                        }
                                        """))
                .andExpect(status().isConflict());
    }

    @Test
    void nonexistentTaskStatusReturnsNotFound() throws Exception {
        mockMvc.perform(
                        get("/api/task_statuses/{id}", Long.MAX_VALUE)
                                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void cannotDeleteStatusAssociatedWithTask() throws Exception {
        Long id = createTestStatus();

        mockMvc.perform(
                        post("/api/tasks")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                            "title": "Integration Task",
                                            "status": "integration_status"
                                        }
                                        """))
                .andExpect(status().isCreated());

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(
                        delete("/api/task_statuses/{id}", id)
                                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isConflict());
    }
}
