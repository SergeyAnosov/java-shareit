package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto create(User user) {
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto update(User user, Long id) {
        return UserMapper.toUserDto(userRepository.update(user, id));
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }

    @Override
    public UserDto getById(Long id) {
        return UserMapper.toUserDto(userRepository.getById(id));
    }
}
