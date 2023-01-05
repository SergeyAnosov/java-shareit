package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User addUser(User user);

    void deleteUser(Long id);

    List<User> getAllUsers();

    User updateUser(User user, Long id);

    User getById(Long id);
}
