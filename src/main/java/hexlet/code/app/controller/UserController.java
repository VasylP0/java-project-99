package hexlet.code.app.controller;

import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserResponseDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.service.UserService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserResponseDTO show(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> index() {
        List<UserResponseDTO> users = userService.findAll();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(users.size()))
                .body(users);
    }

    @PostMapping
    public UserResponseDTO create(@RequestBody UserCreateDTO dto) {

        return userService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@userAuthorization.isOwner(#id, authentication.name)")
    public UserResponseDTO update(
            @PathVariable Long id, @RequestBody UserUpdateDTO dto, Authentication authentication) {

        return userService.update(id, dto, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userAuthorization.isOwner(#id, authentication.name)")
    public UserResponseDTO delete(@PathVariable Long id, Authentication authentication) {

        return userService.delete(id, authentication.getName());
    }
}
