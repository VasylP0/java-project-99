package hexlet.code.app.service.impl;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelResponseDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.LabelMapper;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.service.LabelService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    public LabelServiceImpl(LabelRepository labelRepository, LabelMapper labelMapper) {

        this.labelRepository = labelRepository;
        this.labelMapper = labelMapper;
    }

    private Label findEntityById(Long id) {
        return labelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));
    }

    @Override
    public List<LabelResponseDTO> findAll() {
        return labelRepository.findAll().stream().map(labelMapper::toDTO).toList();
    }

    @Override
    public LabelResponseDTO findById(Long id) {
        return labelMapper.toDTO(findEntityById(id));
    }

    @Override
    public LabelResponseDTO create(LabelCreateDTO data) {
        Label label = labelMapper.toEntity(data);

        return labelMapper.toDTO(labelRepository.saveAndFlush(label));
    }

    @Override
    public LabelResponseDTO update(Long id, LabelUpdateDTO data) {

        Label label = findEntityById(id);

        labelMapper.update(data, label);

        return labelMapper.toDTO(labelRepository.saveAndFlush(label));
    }

    @Override
    public void delete(Long id) {
        Label label = findEntityById(id);

        labelRepository.delete(label);
        labelRepository.flush();
    }
}
