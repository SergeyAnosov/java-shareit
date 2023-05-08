package ru.practicum.shareitgateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.common.Create;
import ru.practicum.shareitgateway.common.Update;
import ru.practicum.shareitgateway.user.dto.UserDto;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("Создаётся пользователь {}", userDto);
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@Validated(Update.class) @RequestBody UserDto userDto, @PathVariable Long id) {
        log.info("Обновляется пользователь {}", userDto);
        return userClient.updateUser(userDto, id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        log.info("Запрос на получение пользователя с id {}", id);
        return userClient.getUserById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Запрос на получение всех пользователей");
        return userClient.getUsers();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Запрос на удаление пользователя с id {}", id);
        userClient.deleteUser(id);
    }
}