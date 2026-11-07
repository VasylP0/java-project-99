
package hexlet.code.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import hexlet.code.app.model.Task;

import java.time.LocalDateTime;

public class TaskResponseDTO {

    private Long id;
    private Integer index;
    private LocalDateTime createdAt;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    private String title;
    private String content;
    private String status;

    public TaskResponseDTO(Task task) {
        this.id = task.getId();
        this.index = task.getIndex();
        this.createdAt = task.getCreatedAt();

        this.assigneeId = task.getAssignee() == null
                ? null : task.getAssignee().getId();

        this.title = task.getName();
        this.content = task.getDescription();
        this.status = task.getTaskStatus().getSlug();
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
}
