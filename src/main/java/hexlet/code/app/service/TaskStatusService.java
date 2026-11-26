package hexlet.code.app.service;

import hexlet.code.app.model.TaskStatus;

import java.util.List;

public interface TaskStatusService {

    List<TaskStatus> findAll();

    TaskStatus findById(Long id);

    TaskStatus create(TaskStatus status);

    TaskStatus update(Long id, TaskStatus updatedStatus);

    void delete(Long id);
}