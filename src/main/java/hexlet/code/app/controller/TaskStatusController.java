package hexlet.code.app.controller;

import hexlet.code.app.repository.TaskStatusRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import hexlet.code.app.model.TaskStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;

@RestController
@RequestMapping("/api/task_statuses")
public class TaskStatusController {

    private final TaskStatusRepository repository;

    public TaskStatusController(TaskStatusRepository repository) {
        this.repository = repository;
    }
    @GetMapping("/{id}")
    public TaskStatus show(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task status not found"
                ));
    }
    @GetMapping
    public List<TaskStatus> index() {
        return repository.findAll();
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatus create(@Valid @RequestBody TaskStatus taskStatus) {
        try {
            return repository.saveAndFlush(taskStatus);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Task status name or slug already exists",
                    e
            );
        }
    }
    @PutMapping("/{id}")
    public TaskStatus update(
            @PathVariable Long id,
            @Valid @RequestBody TaskStatus data) {

        TaskStatus taskStatus = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task status not found"
                ));

        taskStatus.setName(data.getName());
        taskStatus.setSlug(data.getSlug());

        try {
            return repository.saveAndFlush(taskStatus);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Task status name or slug already exists",
                    e
            );
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        TaskStatus taskStatus = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task status not found"
                ));

        try {
            repository.delete(taskStatus);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete a task status that is used by a task",
                    e
            );
        }
    }
}