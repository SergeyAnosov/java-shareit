package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepositoryImpl;

    @Autowired
    public UserServiceImpl(UserRepository userRepositoryImpl) {
        this.userRepositoryImpl = userRepositoryImpl;
    }

    @Override
    public UserDto createUser(User user) {
        return UserMapper.toUserDto(userRepositoryImpl.addUser(user));
    }

    @Override
    public UserDto updateUser(User user, Long id) {
        return UserMapper.toUserDto(userRepositoryImpl.updateUser(user, id));
    }

    @Override
    public List<UserDto> getUsers() {
        List<UserDto> userDto = new ArrayList<>();
        for (User user : userRepositoryImpl.getAllUsers()) {
            userDto.add(UserMapper.toUserDto(user));
        }
        return userDto;
    }

    @Override
    public void deleteUser(Long id) {
        userRepositoryImpl.deleteUser(id);
    }

    @Override
    public UserDto getById(Long id) {
        User user = userRepositoryImpl.getById(id);
        return UserMapper.toUserDto(user);
    }
}
