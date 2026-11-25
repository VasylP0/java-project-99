package hexlet.code.app.controller;

import hexlet.code.app.model.User;
import hexlet.code.app.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User show(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping
    public ResponseEntity<List<User>> index() {
        List<User> users = userService.findAll();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(users.size()))
                .body(users);
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping("/{id}")
    public User update(
            @PathVariable Long id,
            @RequestBody User updatedUser,
            Authentication authentication) {

        return userService.update(
                id, updatedUser, authentication.getName());
    }

    @DeleteMapping("/{id}")
    public User delete(
            @PathVariable Long id,
            Authentication authentication) {

        return userService.delete(
                id, authentication.getName());
    }
}