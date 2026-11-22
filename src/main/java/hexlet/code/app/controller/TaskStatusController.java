
package hexlet.code.app.controller;

import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;

import jakarta.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/task_statuses")
public class TaskStatusController {

    private final TaskStatusRepository repository;

    public TaskStatusController(TaskStatusRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<TaskStatus>> index() {
        List<TaskStatus> statuses = repository.findAll();

        return ResponseEntity.ok()
                .header("X-Total-Count",
                        String.valueOf(statuses.size()))
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
                        "X-Total-Count")
                .body(statuses);
    }

    @GetMapping("/{id}")
    public TaskStatus show(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task status not found"
                ));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatus create(
            @Valid @RequestBody TaskStatus status) {
        try {
            return repository.saveAndFlush(status);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Task status already exists",
                    e
            );
        }
    }

    @PutMapping("/{id}")
    public TaskStatus update(
            @PathVariable Long id,
            @Valid @RequestBody TaskStatus data) {

        TaskStatus status = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task status not found"
                ));

        status.setName(data.getName());
        status.setSlug(data.getSlug());

        try {
            return repository.saveAndFlush(status);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Task status already exists",
                    e
            );
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {

        TaskStatus status = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task status not found"
                ));

        try {
            repository.delete(status);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Task status is in use",
                    e
            );
        }
    }
}
