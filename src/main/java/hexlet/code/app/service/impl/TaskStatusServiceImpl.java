package hexlet.code.app.service.impl;

import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusResponseDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.service.TaskStatusService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TaskStatusServiceImpl implements TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;

    public TaskStatusServiceImpl(
            TaskStatusRepository taskStatusRepository, TaskStatusMapper taskStatusMapper) {

        this.taskStatusRepository = taskStatusRepository;
        this.taskStatusMapper = taskStatusMapper;
    }

    @Override
    public List<TaskStatusResponseDTO> findAll() {
        return taskStatusRepository.findAll().stream().map(taskStatusMapper::toDTO).toList();
    }

    private TaskStatus findEntityById(Long id) {
        return taskStatusRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Task status not found"));
    }

    @Override
    public TaskStatusResponseDTO findById(Long id) {
        return taskStatusMapper.toDTO(findEntityById(id));
    }

    @Override
    public TaskStatusResponseDTO create(TaskStatusCreateDTO dto) {

        TaskStatus status = taskStatusMapper.toEntity(dto);

        return taskStatusMapper.toDTO(taskStatusRepository.saveAndFlush(status));
    }

    @Override
    public TaskStatusResponseDTO update(Long id, TaskStatusUpdateDTO dto) {

        TaskStatus status = findEntityById(id);

        taskStatusMapper.update(dto, status);

        return taskStatusMapper.toDTO(taskStatusRepository.saveAndFlush(status));
    }

    @Override
    public void delete(Long id) {
        TaskStatus status = findEntityById(id);

        taskStatusRepository.delete(status);
        taskStatusRepository.flush();
    }
}
