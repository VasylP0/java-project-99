package hexlet.code.app.service;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.model.Task;

import java.util.List;

public interface TaskService {

    List<Task> findAll(
            String titleCont,
            Long assigneeId,
            String status,
            Long labelId);

    Task findById(Long id);

    Task create(TaskCreateDTO dto);

    Task update(Long id, TaskUpdateDTO dto);

    void delete(Long id);
}