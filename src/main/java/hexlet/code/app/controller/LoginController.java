package hexlet.code.app.controller;

import hexlet.code.app.service.AuthService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    public String login(@RequestBody LoginRequest request) {
        return authService.login(
                request.username(),
                request.password());
    }

    public record LoginRequest(
            String username,
            String password) {
    }
}