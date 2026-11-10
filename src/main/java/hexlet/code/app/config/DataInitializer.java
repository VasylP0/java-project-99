
package hexlet.code.app.config;

import hexlet.code.app.model.User;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.TaskStatus;

import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskStatusRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initAdmin(
            UserRepository userRepository,
            LabelRepository labelRepository,
            TaskStatusRepository taskStatusRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // Create the default Hexlet administrator
            if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
                User admin = new User();
                admin.setFirstName("Admin");
                admin.setLastName("Hexlet");
                admin.setEmail("hexlet@example.com");
                admin.setPassword(passwordEncoder.encode("qwerty"));
                admin.setAdmin(true);

                userRepository.save(admin);
            }

            // Optional administrator from environment variables
            String email = System.getenv("ADMIN_EMAIL");
            String password = System.getenv("ADMIN_PASSWORD");

            if (email != null && !email.isBlank()
                    && password != null && !password.isBlank()
                    && !email.equalsIgnoreCase("hexlet@example.com")) {

                if (userRepository.findByEmail(email).isEmpty()) {
                    User admin = new User();
                    admin.setFirstName("Admin");
                    admin.setLastName("User");
                    admin.setEmail(email);
                    admin.setPassword(passwordEncoder.encode(password));
                    admin.setAdmin(true);

                    userRepository.save(admin);
                }
            }

            // Create default task statuses
            createStatus(taskStatusRepository, "Draft", "draft");
            createStatus(taskStatusRepository, "To Review", "to_review");
            createStatus(taskStatusRepository, "To Be Fixed", "to_be_fixed");
            createStatus(taskStatusRepository, "To Publish", "to_publish");
            createStatus(taskStatusRepository, "Published", "published");

            // Create default labels
            if (!labelRepository.existsByName("feature")) {
                Label feature = new Label();
                feature.setName("feature");
                labelRepository.save(feature);
            }

            if (!labelRepository.existsByName("bugs")) {
                Label bugs = new Label();
                bugs.setName("bugs");
                labelRepository.save(bugs);
            }
        };
    }

    private void createStatus(
            TaskStatusRepository repository,
            String name,
            String slug) {

        if (repository.findBySlug(slug).isEmpty()) {
            TaskStatus status = new TaskStatus();
            status.setName(name);
            status.setSlug(slug);
            repository.save(status);
        }
    }
}
