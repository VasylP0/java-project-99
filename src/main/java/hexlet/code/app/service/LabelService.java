package hexlet.code.app.service;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelResponseDTO;
import hexlet.code.app.dto.LabelUpdateDTO;

import java.util.List;

public interface LabelService {

    List<LabelResponseDTO> findAll();

    LabelResponseDTO findById(Long id);

    LabelResponseDTO create(LabelCreateDTO data);

    LabelResponseDTO update(Long id, LabelUpdateDTO data);

    void delete(Long id);
}