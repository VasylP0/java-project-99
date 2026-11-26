package hexlet.code.app.controller;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.dto.LabelResponseDTO;
import hexlet.code.app.model.Label;
import hexlet.code.app.service.LabelService;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
public class LabelController {

    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
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
        List<LabelResponseDTO> labels = labelService.findAll()
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
        return toDTO(labelService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponseDTO create(
            @Valid @RequestBody LabelCreateDTO data) {

        Label label = new Label();
        label.setName(data.getName());

        return toDTO(labelService.create(label));
    }

    @PutMapping("/{id}")
    public LabelResponseDTO update(
            @PathVariable Long id,
            @Valid @RequestBody LabelUpdateDTO data) {

        Label label = new Label();
        label.setName(data.getName());

        return toDTO(labelService.update(id, label));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        labelService.delete(id);
    }
}