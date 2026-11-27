package hexlet.code.app.controller;

import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.repository.LabelRepository;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private EntityManager entityManager;

    private String adminToken;
    private Long adminId;
    private Long featureId;
    private Long bugsId;

    @BeforeEach
    void setUp() throws Exception {
        adminToken = mockMvc.perform(post("/api/login")
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

        adminId = userRepository.findByEmail("hexlet@example.com")
                .orElseThrow()
                .getId();

        featureId = labelRepository.findAll().stream()
                .filter(label -> "feature".equals(label.getName()))
                .findFirst()
                .orElseThrow()
                .getId();

        bugsId = labelRepository.findAll().stream()
                .filter(label -> "bugs".equals(label.getName()))
                .findFirst()
                .orElseThrow()
                .getId();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private Long createTestTask() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Integration Task",
                                    "index": 10,
                                    "content": "Integration description",
                                    "status": "draft",
                                    "assignee_id": %d,
                                    "labels": [%d, %d]
                                }
                                """.formatted(adminId, featureId, bugsId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Integration Task"))
                .andExpect(jsonPath("$.index").value(10))
                .andExpect(jsonPath("$.content")
                        .value("Integration description"))
                .andExpect(jsonPath("$.status").value("draft"))
                .andExpect(jsonPath("$.assignee_id")
                        .value(adminId.intValue()))
                .andExpect(jsonPath("$.labels", hasSize(2)))
                .andExpect(jsonPath("$.labels", hasItems(
                        featureId.intValue(), bugsId.intValue())));

        flushAndClear();

        return taskRepository.findAll().stream()
                .filter(task -> "Integration Task".equals(task.getName()))
                .findFirst()
                .orElseThrow()
                .getId();
    }

    @Test
    void listTasks() throws Exception {
        createTestTask();

        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void createAndShowTask() throws Exception {
        Long id = createTestTask();

        mockMvc.perform(get("/api/tasks/{id}", id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.intValue()))
                .andExpect(jsonPath("$.title").value("Integration Task"))
                .andExpect(jsonPath("$.status").value("draft"))
                .andExpect(jsonPath("$.assignee_id")
                        .value(adminId.intValue()))
                .andExpect(jsonPath("$.labels", hasSize(2)))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void createTaskWithoutOptionalRelationships() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Minimal Task",
                                    "status": "draft"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Minimal Task"))
                .andExpect(jsonPath("$.status").value("draft"))
                .andExpect(jsonPath("$.assignee_id").doesNotExist())
                .andExpect(jsonPath("$.labels", hasSize(0)));
    }

    @Test
    void partiallyUpdateTask() throws Exception {
        Long id = createTestTask();

        mockMvc.perform(put("/api/tasks/{id}", id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Updated Integration Task",
                                    "status": "published"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title")
                        .value("Updated Integration Task"))
                .andExpect(jsonPath("$.status").value("published"))
                .andExpect(jsonPath("$.index").value(10))
                .andExpect(jsonPath("$.content")
                        .value("Integration description"))
                .andExpect(jsonPath("$.assignee_id")
                        .value(adminId.intValue()))
                .andExpect(jsonPath("$.labels", hasSize(2)));

        flushAndClear();

        mockMvc.perform(get("/api/tasks/{id}", id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title")
                        .value("Updated Integration Task"))
                .andExpect(jsonPath("$.status").value("published"));
    }

    @Test
    void clearTaskLabels() throws Exception {
        Long id = createTestTask();

        mockMvc.perform(put("/api/tasks/{id}", id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "labels": []
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labels", hasSize(0)));

        flushAndClear();

        mockMvc.perform(get("/api/tasks/{id}", id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labels", hasSize(0)));
    }

    @Test
    void deleteTask() throws Exception {
        Long id = createTestTask();

        mockMvc.perform(delete("/api/tasks/{id}", id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        flushAndClear();

        mockMvc.perform(get("/api/tasks/{id}", id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void filterTasksByTitle() throws Exception {
        Long id = createTestTask();

        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("titleCont", "Integration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(id.intValue()));
    }

    @Test
    void filterTasksByStatus() throws Exception {
        Long id = createTestTask();

        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("status", "draft"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItems(id.intValue())));
    }

    @Test
    void filterTasksByAssignee() throws Exception {
        Long id = createTestTask();

        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("assigneeId", adminId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItems(id.intValue())));
    }

    @Test
    void filterTasksByLabel() throws Exception {
        Long id = createTestTask();

        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("labelId", featureId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItems(id.intValue())));
    }

    @Test
    void filterTasksByCombinedParameters() throws Exception {
        Long id = createTestTask();

        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Integration Other Task",
                                    "status": "published"
                                }
                                """))
                .andExpect(status().isCreated());

        flushAndClear();

        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("titleCont", "Integration")
                        .param("status", "draft")
                        .param("assigneeId", adminId.toString())
                        .param("labelId", featureId.toString()))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(id.intValue()));
    }

    @Test
    void filterTasksWithoutMatches() throws Exception {
        createTestTask();

        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("titleCont", "nonexistent-task-title"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "0"))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void rejectBlankTitle() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "",
                                    "status": "draft"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectBlankStatus() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Invalid Task",
                                    "status": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectInvalidStatus() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Invalid Task",
                                    "status": "nonexistent_status"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectInvalidAssignee() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Invalid Task",
                                    "status": "draft",
                                    "assignee_id": %d
                                }
                                """.formatted(Long.MAX_VALUE)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectInvalidLabel() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Invalid Task",
                                    "status": "draft",
                                    "labels": [%d]
                                }
                                """.formatted(Long.MAX_VALUE)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void nonexistentTaskReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}", Long.MAX_VALUE)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}