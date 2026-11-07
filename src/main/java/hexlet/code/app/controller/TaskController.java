
package hexlet.code.app.controller;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskResponseDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;

    public TaskController(
            TaskRepository taskRepository,
            TaskStatusRepository taskStatusRepository,
            UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<TaskResponseDTO> index() {
        return taskRepository.findAll()
                .stream()
                .map(TaskResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public TaskResponseDTO show(@PathVariable Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task not found"));

        return new TaskResponseDTO(task);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDTO create(
            @Valid @RequestBody TaskCreateDTO dto) {

        TaskStatus status = taskStatusRepository
                .findBySlug(dto.getStatus())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Invalid status"));

        Task task = new Task();
        task.setName(dto.getTitle());
        task.setIndex(dto.getIndex());
        task.setDescription(dto.getContent());
        task.setTaskStatus(status);

        if (dto.getAssigneeId() != null) {
            User assignee = userRepository
                    .findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Invalid assignee"));

            task.setAssignee(assignee);
        }

        return new TaskResponseDTO(taskRepository.save(task));
    }

    @PutMapping("/{id}")
    public TaskResponseDTO update(
            @PathVariable Long id,
            @RequestBody TaskUpdateDTO dto) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task not found"));

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
                            HttpStatus.BAD_REQUEST, "Invalid status"));

            task.setTaskStatus(status);
        }

        if (dto.getAssigneeId() != null) {
            User assignee = userRepository
                    .findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Invalid assignee"));

            task.setAssignee(assignee);
        }

        return new TaskResponseDTO(taskRepository.save(task));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task not found"));

        taskRepository.delete(task);
    }
}
