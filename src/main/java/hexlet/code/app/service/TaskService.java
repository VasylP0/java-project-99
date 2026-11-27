package hexlet.code.app.service;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskResponseDTO;
import hexlet.code.app.dto.TaskUpdateDTO;

import java.util.List;

public interface TaskService {

    List<TaskResponseDTO> findAll(
            String titleCont,
            Long assigneeId,
            String status,
            Long labelId);

    TaskResponseDTO findById(Long id);

    TaskResponseDTO create(TaskCreateDTO dto);

    TaskResponseDTO update(Long id, TaskUpdateDTO dto);

    void delete(Long id);
}