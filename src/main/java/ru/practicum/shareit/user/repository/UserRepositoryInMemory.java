package ru.practicum.shareit.user.repository;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Repository
public class UserRepositoryInMemory implements UserRepository {
    private Map<Long, User> users = new HashMap<>();
    private long count = 1;

    @Override
    public User addUser(User user) {
        checkEmail(user);
        user.setId(count);
        count++;
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User updateUser(User newUser, Long id) {
        if (users.containsKey(id)) {
            User user = getById(id);
            if (newUser.getName() == null) {
                newUser.setName(user.getName());
                newUser.setId(id);
            }
            if (newUser.getEmail() == null) {
                newUser.setEmail(user.getEmail());
                newUser.setId(id);
            } else {
                checkEmail(newUser);
            }
            user.setName(newUser.getName());
            user.setEmail(newUser.getEmail());
            users.put(id, user);
            return user;
        } else {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Такого юзера не существует");
        }
    }

    @Override
    public User getById(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Такого юзера не существует");
        }
    }

    private void checkEmail(User user) {
        for (User checkUser : users.values()) {
            if (user.getEmail().equals(checkUser.getEmail())) {
                throw new EmailExistException("пользователь с таким email уже существует");
            }
        }
    }
}
