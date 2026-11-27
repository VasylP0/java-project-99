package hexlet.code.app.dto;

import java.time.LocalDateTime;

public class TaskStatusResponseDTO {

    private final Long id;
    private final String name;
    private final String slug;
    private final LocalDateTime createdAt;

    public TaskStatusResponseDTO(
            Long id,
            String name,
            String slug,
            LocalDateTime createdAt) {

        this.id = id;
        this.name = name;
        this.slug = slug;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}