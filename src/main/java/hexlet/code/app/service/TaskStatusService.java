package hexlet.code.app.service;

import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.dto.TaskStatusResponseDTO;

import java.util.List;

public interface TaskStatusService {

    List<TaskStatusResponseDTO> findAll();

    TaskStatusResponseDTO findById(Long id);

    TaskStatusResponseDTO create(TaskStatusCreateDTO dto);

    TaskStatusResponseDTO update(Long id, TaskStatusUpdateDTO dto);

    void delete(Long id);
}