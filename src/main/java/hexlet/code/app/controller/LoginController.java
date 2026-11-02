
package hexlet.code.app.controller;

import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public LoginController(UserRepository userRepository,
                           JwtService jwtService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public String login(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.username())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED));

        if (request.password() == null
                || !passwordEncoder.matches(
                request.password(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED);
        }

        return jwtService.generateToken(user.getEmail());
    }

    public record LoginRequest(String username, String password) {
    }
}
