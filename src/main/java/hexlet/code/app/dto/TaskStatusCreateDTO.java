package hexlet.code.app.dto;

import jakarta.validation.constraints.NotBlank;

public class TaskStatusCreateDTO {

    @NotBlank private String name;

    @NotBlank private String slug;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
