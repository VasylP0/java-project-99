package hexlet.code.app.support;

import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestDatabaseCleaner {

    private final TaskRepository taskRepository;
    private final LabelRepository labelRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;
    private final CommandLineRunner initializer;

    public TestDatabaseCleaner(
            TaskRepository taskRepository,
            LabelRepository labelRepository,
            TaskStatusRepository taskStatusRepository,
            UserRepository userRepository,
            @Qualifier("initAdmin") CommandLineRunner initializer) {

        this.taskRepository = taskRepository;
        this.labelRepository = labelRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.userRepository = userRepository;
        this.initializer = initializer;
    }

    public void reset() throws Exception {
        taskRepository.deleteAll();
        labelRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();

        initializer.run();
    }
}
