package hexlet.code.app.service.impl;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskResponseDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.exception.InvalidRequestException;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.TaskService;
import hexlet.code.app.specification.TaskSpecification;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;
    private final TaskMapper taskMapper;

    public TaskServiceImpl(
            TaskRepository taskRepository,
            TaskStatusRepository taskStatusRepository,
            UserRepository userRepository,
            LabelRepository labelRepository,
            TaskMapper taskMapper) {

        this.taskRepository = taskRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.userRepository = userRepository;
        this.labelRepository = labelRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public List<TaskResponseDTO> findAll(
            String titleCont, Long assigneeId, String status, Long labelId) {

        Specification<Task> spec = (root, query, cb) -> cb.conjunction();

        if (titleCont != null) {
            spec = spec.and(TaskSpecification.titleContains(titleCont));
        }

        if (assigneeId != null) {
            spec = spec.and(TaskSpecification.hasAssignee(assigneeId));
        }

        if (status != null) {
            spec = spec.and(TaskSpecification.hasStatus(status));
        }

        if (labelId != null) {
            spec = spec.and(TaskSpecification.hasLabel(labelId));
        }

        return taskRepository.findAll(spec).stream().map(taskMapper::toDTO).toList();
    }

    private Task findEntityById(Long id) {
        return taskRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    @Override
    public TaskResponseDTO findById(Long id) {
        return taskMapper.toDTO(findEntityById(id));
    }

    @Override
    public TaskResponseDTO create(TaskCreateDTO dto) {

        Task task = taskMapper.toEntity(dto);

        task.setTaskStatus(resolveStatus(dto.getStatus()));

        if (dto.getAssigneeId() != null) {
            task.setAssignee(resolveAssignee(dto.getAssigneeId()));
        }

        if (dto.getLabels() != null) {
            task.setLabels(resolveLabels(dto.getLabels()));
        }

        return taskMapper.toDTO(taskRepository.save(task));
    }

    @Override
    public TaskResponseDTO update(Long id, TaskUpdateDTO dto) {

        Task task = findEntityById(id);

        taskMapper.update(dto, task);

        if (dto.getStatus() != null) {
            task.setTaskStatus(resolveStatus(dto.getStatus()));
        }

        if (dto.getAssigneeId() != null) {
            task.setAssignee(resolveAssignee(dto.getAssigneeId()));
        }

        if (dto.getLabels() != null) {
            task.setLabels(resolveLabels(dto.getLabels()));
        }

        return taskMapper.toDTO(taskRepository.save(task));
    }

    @Override
    public void delete(Long id) {
        Task task = findEntityById(id);
        taskRepository.delete(task);
    }

    private TaskStatus resolveStatus(String slug) {
        return taskStatusRepository
                .findBySlug(slug)
                .orElseThrow(() -> new InvalidRequestException("Invalid status"));
    }

    private User resolveAssignee(Long assigneeId) {
        return userRepository
                .findById(assigneeId)
                .orElseThrow(() -> new InvalidRequestException("Invalid assignee"));
    }

    private Set<Label> resolveLabels(Set<Long> labelIds) {

        Set<Label> labels = new HashSet<>(labelRepository.findAllById(labelIds));

        if (labels.size() != labelIds.size()) {
            throw new InvalidRequestException("Invalid label IDs");
        }

        return labels;
    }
}
