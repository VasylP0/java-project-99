
package hexlet.code.app.controller;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskResponseDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.specification.TaskSpecification;

import jakarta.validation.Valid;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;

    public TaskController(
            TaskRepository taskRepository,
            TaskStatusRepository taskStatusRepository,
            UserRepository userRepository,
            LabelRepository labelRepository) {

        this.taskRepository = taskRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.userRepository = userRepository;
        this.labelRepository = labelRepository;
    }

    private Set<Label> resolveLabels(Set<Long> labelIds) {
        Set<Label> labels = new HashSet<>();

        for (Long labelId : labelIds) {
            Label label = labelRepository.findById(labelId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Invalid label ID: " + labelId));

            labels.add(label);
        }

        return labels;
    }

    @GetMapping
    public List<TaskResponseDTO> index(
            @RequestParam(required = false) String titleCont,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long labelId) {

        Specification<Task> spec =
                (root, query, cb) -> cb.conjunction();

        if (titleCont != null) {
            spec = spec.and(
                    TaskSpecification.titleContains(titleCont));
        }

        if (assigneeId != null) {
            spec = spec.and(
                    TaskSpecification.hasAssignee(assigneeId));
        }

        if (status != null) {
            spec = spec.and(
                    TaskSpecification.hasStatus(status));
        }

        if (labelId != null) {
            spec = spec.and(
                    TaskSpecification.hasLabel(labelId));
        }

        return taskRepository.findAll(spec)
                .stream()
                .map(TaskResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public TaskResponseDTO show(@PathVariable Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found"));

        return new TaskResponseDTO(task);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDTO create(
            @Valid @RequestBody TaskCreateDTO dto) {

        TaskStatus status = taskStatusRepository
                .findBySlug(dto.getStatus())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid status"));

        Task task = new Task();
        task.setName(dto.getTitle());
        task.setIndex(dto.getIndex());
        task.setDescription(dto.getContent());
        task.setTaskStatus(status);

        if (dto.getAssigneeId() != null) {
            User assignee = userRepository
                    .findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Invalid assignee"));

            task.setAssignee(assignee);
        }

        if (dto.getLabels() != null) {
            task.setLabels(resolveLabels(dto.getLabels()));
        }

        return new TaskResponseDTO(taskRepository.save(task));
    }

    @PutMapping("/{id}")
    public TaskResponseDTO update(
            @PathVariable Long id,
            @RequestBody TaskUpdateDTO dto) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found"));

        if (dto.getTitle() != null) {
            task.setName(dto.getTitle());
        }

        if (dto.getIndex() != null) {
            task.setIndex(dto.getIndex());
        }

        if (dto.getContent() != null) {
            task.setDescription(dto.getContent());
        }

        if (dto.getStatus() != null) {
            TaskStatus status = taskStatusRepository
                    .findBySlug(dto.getStatus())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Invalid status"));

            task.setTaskStatus(status);
        }

        if (dto.getAssigneeId() != null) {
            User assignee = userRepository
                    .findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Invalid assignee"));

            task.setAssignee(assignee);
        }

        if (dto.getLabels() != null) {
            task.setLabels(resolveLabels(dto.getLabels()));
        }

        return new TaskResponseDTO(taskRepository.save(task));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found"));

        taskRepository.delete(task);
    }
}
