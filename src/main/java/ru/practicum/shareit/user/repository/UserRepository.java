package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User save(User user);

    void delete(Long id);

    List<User> getAll();

    User update(User user, Long id);

    User getById(Long id);
}
