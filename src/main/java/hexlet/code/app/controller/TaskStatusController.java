package hexlet.code.app.controller;

import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.service.TaskStatusService;

import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task_statuses")
public class TaskStatusController {

    private final TaskStatusService taskStatusService;

    public TaskStatusController(TaskStatusService taskStatusService) {
        this.taskStatusService = taskStatusService;
    }

    @GetMapping
    public ResponseEntity<List<TaskStatus>> index() {
        List<TaskStatus> statuses = taskStatusService.findAll();

        return ResponseEntity.ok()
                .header("X-Total-Count",
                        String.valueOf(statuses.size()))
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
                        "X-Total-Count")
                .body(statuses);
    }

    @GetMapping("/{id}")
    public TaskStatus show(@PathVariable Long id) {
        return taskStatusService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatus create(
            @Valid @RequestBody TaskStatus status) {

        return taskStatusService.create(status);
    }

    @PutMapping("/{id}")
    public TaskStatus update(
            @PathVariable Long id,
            @Valid @RequestBody TaskStatus data) {

        return taskStatusService.update(id, data);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskStatusService.delete(id);
    }
}