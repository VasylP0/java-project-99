package hexlet.code.app.service.impl;

import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.service.LabelService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;

    public LabelServiceImpl(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    @Override
    public List<Label> findAll() {
        return labelRepository.findAll();
    }

    @Override
    public Label findById(Long id) {
        return labelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Label not found"));
    }

    @Override
    public Label create(Label label) {
        return labelRepository.saveAndFlush(label);
    }

    @Override
    public Label update(Long id, Label updatedLabel) {
        Label label = findById(id);

        label.setName(updatedLabel.getName());

        return labelRepository.saveAndFlush(label);
    }

    @Override
    public void delete(Long id) {
        Label label = findById(id);

        labelRepository.delete(label);
        labelRepository.flush();
    }
}