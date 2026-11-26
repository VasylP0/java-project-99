package hexlet.code.app.service.impl;

import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.service.TaskStatusService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TaskStatusServiceImpl implements TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;

    public TaskStatusServiceImpl(
            TaskStatusRepository taskStatusRepository) {

        this.taskStatusRepository = taskStatusRepository;
    }

    @Override
    public List<TaskStatus> findAll() {
        return taskStatusRepository.findAll();
    }

    @Override
    public TaskStatus findById(Long id) {
        return taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task status not found"));
    }

    @Override
    public TaskStatus create(TaskStatus status) {
        return taskStatusRepository.saveAndFlush(status);
    }

    @Override
    public TaskStatus update(Long id, TaskStatus updatedStatus) {
        TaskStatus status = findById(id);

        status.setName(updatedStatus.getName());
        status.setSlug(updatedStatus.getSlug());

        return taskStatusRepository.saveAndFlush(status);
    }

    @Override
    public void delete(Long id) {
        TaskStatus status = findById(id);

        taskStatusRepository.delete(status);
        taskStatusRepository.flush();
    }
}