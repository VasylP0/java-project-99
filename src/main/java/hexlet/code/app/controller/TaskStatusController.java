package hexlet.code.app.controller;

import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.dto.TaskStatusResponseDTO;
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
    public ResponseEntity<List<TaskStatusResponseDTO>> index() {
        List<TaskStatusResponseDTO> statuses =
                taskStatusService.findAll();

        return ResponseEntity.ok()
                .header("X-Total-Count",
                        String.valueOf(statuses.size()))
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
                        "X-Total-Count")
                .body(statuses);
    }

    @GetMapping("/{id}")
    public TaskStatusResponseDTO show(@PathVariable Long id) {
        return taskStatusService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatusResponseDTO create(
            @Valid @RequestBody TaskStatusCreateDTO dto) {

        return taskStatusService.create(dto);
    }

    @PutMapping("/{id}")
    public TaskStatusResponseDTO update(
            @PathVariable Long id,
            @Valid @RequestBody TaskStatusUpdateDTO dto) {

        return taskStatusService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskStatusService.delete(id);
    }
}