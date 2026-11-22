
package hexlet.code.app.controller;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.dto.LabelResponseDTO;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;

import jakarta.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
public class LabelController {

    private final LabelRepository repository;

    public LabelController(LabelRepository repository) {
        this.repository = repository;
    }

    private LabelResponseDTO toDTO(Label label) {
        return new LabelResponseDTO(
                label.getId(),
                label.getName(),
                label.getCreatedAt()
        );
    }

    @GetMapping
    public ResponseEntity<List<LabelResponseDTO>> index() {
        List<LabelResponseDTO> labels = repository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();

        return ResponseEntity.ok()
                .header("X-Total-Count",
                        String.valueOf(labels.size()))
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
                        "X-Total-Count")
                .body(labels);
    }

    @GetMapping("/{id}")
    public LabelResponseDTO show(@PathVariable Long id) {
        Label label = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Label not found"
                ));

        return toDTO(label);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponseDTO create(
            @Valid @RequestBody LabelCreateDTO data) {

        Label label = new Label();
        label.setName(data.getName());

        try {
            return toDTO(repository.saveAndFlush(label));
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Label name already exists",
                    e
            );
        }
    }

    @PutMapping("/{id}")
    public LabelResponseDTO update(
            @PathVariable Long id,
            @Valid @RequestBody LabelUpdateDTO data) {

        Label label = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Label not found"
                ));

        label.setName(data.getName());

        try {
            return toDTO(repository.saveAndFlush(label));
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Label name already exists",
                    e
            );
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {

        Label label = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Label not found"
                ));

        try {
            repository.delete(label);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete a label used by a task",
                    e
            );
        }
    }
}
