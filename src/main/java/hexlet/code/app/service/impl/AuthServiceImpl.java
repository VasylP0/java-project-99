package hexlet.code.app.service.impl;

import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.security.JwtService;
import hexlet.code.app.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(
            UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String login(String username, String password) {

        User user =
                userRepository
                        .findByEmail(username)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (password == null || !passwordEncoder.matches(password, user.getPassword())) {

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return jwtService.generateToken(user.getEmail());
    }
}
