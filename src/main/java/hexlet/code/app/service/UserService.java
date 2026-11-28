package hexlet.code.app.service;

import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserResponseDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import java.util.List;

public interface UserService {

    List<UserResponseDTO> findAll();

    UserResponseDTO findById(Long id);

    UserResponseDTO create(UserCreateDTO dto);

    UserResponseDTO update(Long id, UserUpdateDTO dto);

    UserResponseDTO delete(Long id);
}
