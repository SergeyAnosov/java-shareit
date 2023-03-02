package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto save(UserDto userDto);

    UserDto update(UserDto userDto, Long id);

    List<UserDto> getAll();

    void delete(Long id);

    UserDto getById(Long id);
}
