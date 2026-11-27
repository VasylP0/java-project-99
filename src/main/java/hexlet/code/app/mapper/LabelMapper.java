package hexlet.code.app.mapper;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.dto.LabelResponseDTO;
import hexlet.code.app.model.Label;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LabelMapper {

    LabelResponseDTO toDTO(Label label);

    @Mapping(target = "id", ignore = true)
    Label toEntity(LabelCreateDTO data);

    @Mapping(target = "id", ignore = true)
    void update(LabelUpdateDTO data, @MappingTarget Label label);
}
