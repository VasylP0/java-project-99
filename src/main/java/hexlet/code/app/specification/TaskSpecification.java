
package hexlet.code.app.specification;

import hexlet.code.app.model.Task;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecification {

    public static Specification<Task> titleContains(String title) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("name")),
                        "%" + title.toLowerCase() + "%"
                );
    }

    public static Specification<Task> hasAssignee(Long assigneeId) {
        return (root, query, cb) ->
                cb.equal(root.get("assignee").get("id"), assigneeId);
    }

    public static Specification<Task> hasStatus(String status) {
        return (root, query, cb) ->
                cb.equal(root.get("taskStatus").get("slug"), status);
    }

    public static Specification<Task> hasLabel(Long labelId) {
        return (root, query, cb) ->
                cb.equal(root.join("labels").get("id"), labelId);
    }
}
