package hexlet.code.app.service.impl;

import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.dto.UserResponseDTO;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .toList();
    }

    private User findEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"));
    }

    @Override
    public UserResponseDTO findById(Long id) {
        return userMapper.toDTO(findEntityById(id));
    }

    @Override
    public UserResponseDTO create(UserCreateDTO dto) {

        User user = userMapper.toEntity(dto);

        user.setAdmin(false);

        user.setPassword(
                passwordEncoder.encode(dto.getPassword()));

        return userMapper.toDTO(
                userRepository.save(user));
    }

    @Override
    public UserResponseDTO update(
            Long id,
            UserUpdateDTO dto,
            String currentUserEmail) {

        User user = findEntityById(id);

        if (!user.getEmail().equals(currentUserEmail)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Access denied");
        }

        userMapper.update(dto, user);

        return userMapper.toDTO(
                userRepository.save(user));
    }

    @Override
    public UserResponseDTO delete(
            Long id,
            String currentUserEmail) {

        User user = findEntityById(id);

        User currentUser = userRepository
                .findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not found"));

        if (!currentUser.isAdmin()
                && !user.getEmail().equals(currentUserEmail)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Access denied");
        }

        UserResponseDTO response = userMapper.toDTO(user);

        userRepository.delete(user);
        userRepository.flush();

        return response;
    }
}