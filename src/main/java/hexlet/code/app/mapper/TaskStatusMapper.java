package hexlet.code.app.mapper;

import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.dto.TaskStatusResponseDTO;
import hexlet.code.app.model.TaskStatus;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskStatusMapper {

    TaskStatusResponseDTO toDTO(TaskStatus status);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    TaskStatus toEntity(TaskStatusCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void update(
            TaskStatusUpdateDTO dto,
            @MappingTarget TaskStatus status);
}