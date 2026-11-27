package hexlet.code.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.Set;

public class TaskResponseDTO {

    private Long id;
    private Integer index;
    private LocalDateTime createdAt;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    private String title;
    private String content;
    private String status;
    private Set<Long> labels;

    public TaskResponseDTO(
            Long id,
            Integer index,
            LocalDateTime createdAt,
            Long assigneeId,
            String title,
            String content,
            String status,
            Set<Long> labels) {

        this.id = id;
        this.index = index;
        this.createdAt = createdAt;
        this.assigneeId = assigneeId;
        this.title = title;
        this.content = content;
        this.status = status;
        this.labels = labels;
    }

    public Long getId() {
        return id;
    }

    public Integer getIndex() {
        return index;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getStatus() {
        return status;
    }

    public Set<Long> getLabels() {
        return labels;
    }
}
