package hexlet.code.app.controller;

import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertTrue;
import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskResponseDTO;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import hexlet.code.app.dto.TaskUpdateDTO;
import static org.mockito.Mockito.verify;

class TaskControllerTest {

    @Test
    void testIndex() {
        TaskRepository taskRepository =
                Mockito.mock(TaskRepository.class);

        TaskStatusRepository statusRepository =
                Mockito.mock(TaskStatusRepository.class);

        UserRepository userRepository =
                Mockito.mock(UserRepository.class);

        TaskController controller = new TaskController(
                taskRepository,
                statusRepository,
                userRepository
        );

        assertTrue(controller.index().isEmpty());
    }
    @Test
    void testCreate() {
        TaskRepository taskRepository =
                Mockito.mock(TaskRepository.class);

        TaskStatusRepository statusRepository =
                Mockito.mock(TaskStatusRepository.class);

        UserRepository userRepository =
                Mockito.mock(UserRepository.class);

        TaskController controller = new TaskController(
                taskRepository,
                statusRepository,
                userRepository
        );

        TaskStatus status = new TaskStatus();
        status.setSlug("new");

        when(statusRepository.findBySlug("new"))
                .thenReturn(Optional.of(status));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TaskCreateDTO dto = new TaskCreateDTO();
        dto.setTitle("Test task");
        dto.setContent("Test description");
        dto.setStatus("new");

        TaskResponseDTO result = controller.create(dto);

        assertEquals("Test task", result.getTitle());
        assertEquals("Test description", result.getContent());
        assertEquals("new", result.getStatus());
    }
    @Test
    void testShow() {
        TaskRepository taskRepository =
                Mockito.mock(TaskRepository.class);

        TaskStatusRepository statusRepository =
                Mockito.mock(TaskStatusRepository.class);

        UserRepository userRepository =
                Mockito.mock(UserRepository.class);

        TaskController controller = new TaskController(
                taskRepository,
                statusRepository,
                userRepository
        );

        TaskStatus status = new TaskStatus();
        status.setSlug("new");

        Task task = new Task();
        task.setName("My task");
        task.setDescription("My description");
        task.setTaskStatus(status);

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        TaskResponseDTO result = controller.show(1L);

        assertEquals("My task", result.getTitle());
        assertEquals("My description", result.getContent());
        assertEquals("new", result.getStatus());
    }
    @Test
    void testUpdate() {
        TaskRepository taskRepository =
                Mockito.mock(TaskRepository.class);

        TaskStatusRepository statusRepository =
                Mockito.mock(TaskStatusRepository.class);

        UserRepository userRepository =
                Mockito.mock(UserRepository.class);

        TaskController controller = new TaskController(
                taskRepository,
                statusRepository,
                userRepository
        );

        TaskStatus status = new TaskStatus();
        status.setSlug("new");

        Task task = new Task();
        task.setName("Old title");
        task.setDescription("Old description");
        task.setTaskStatus(status);

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TaskUpdateDTO dto = new TaskUpdateDTO();
        dto.setTitle("Updated title");

        TaskResponseDTO result = controller.update(1L, dto);

        assertEquals("Updated title", result.getTitle());
        assertEquals("Old description", result.getContent());
        assertEquals("new", result.getStatus());
    }
    @Test
    void testDelete() {
        TaskRepository taskRepository =
                Mockito.mock(TaskRepository.class);

        TaskStatusRepository statusRepository =
                Mockito.mock(TaskStatusRepository.class);

        UserRepository userRepository =
                Mockito.mock(UserRepository.class);

        TaskController controller = new TaskController(
                taskRepository,
                statusRepository,
                userRepository
        );

        Task task = new Task();

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        controller.delete(1L);

        verify(taskRepository).delete(task);
    }
}