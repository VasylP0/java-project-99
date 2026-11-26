package hexlet.code.app.service;

import hexlet.code.app.model.Label;

import java.util.List;

public interface LabelService {

    List<Label> findAll();

    Label findById(Long id);

    Label create(Label label);

    Label update(Long id, Label updatedLabel);

    void delete(Long id);
}