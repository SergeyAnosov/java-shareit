package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    UserDto createUser(User user);

    UserDto updateUser(User user, Long id);

    List<UserDto> getUsers();

    void deleteUser(Long id);

    UserDto getById(Long id);
}
