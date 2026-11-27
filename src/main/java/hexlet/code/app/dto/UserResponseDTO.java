package hexlet.code.app.dto;

import java.time.LocalDateTime;

public class UserResponseDTO {

    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final boolean admin;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public UserResponseDTO(
            Long id,
            String firstName,
            String lastName,
            String email,
            boolean admin,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.admin = admin;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAdmin() {
        return admin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}