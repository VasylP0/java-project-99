package hexlet.code.app.service;

import hexlet.code.app.model.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findById(Long id);

    User create(User user);

    User update(Long id, User updatedUser, String currentUserEmail);

    User delete(Long id, String currentUserEmail);
}