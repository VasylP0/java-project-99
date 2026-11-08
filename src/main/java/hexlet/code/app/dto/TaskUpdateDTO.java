
package hexlet.code.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public class TaskUpdateDTO {

    private String title;
    private Integer index;
    private String content;
    private String status;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    private Set<Long> labels;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public Set<Long> getLabels() {
        return labels;
    }

    public void setLabels(Set<Long> labels) {
        this.labels = labels;
    }
}
