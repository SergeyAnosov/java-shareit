package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIT {
    @Autowired
    private final UserService service;

    private UserDto getUserDto() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Вася");
        userDto.setEmail("Email@mail.ru");
        return userDto;
    }

    @Test
    @Transactional
    void save_update_getAll_delete() {
        UserDto savedUserDto = service.save(getUserDto());

        assertEquals(savedUserDto.getId(), 1L);
        assertEquals(savedUserDto.getName(), "Вася");
        assertEquals(savedUserDto.getEmail(), "Email@mail.ru");

        UserDto toUpdate = new UserDto();
        toUpdate.setName("update");
        toUpdate.setEmail("update@user.com");

        UserDto update = service.update(toUpdate, 1L);

        assertEquals(update.getId(), 1L);
        assertEquals(update.getName(), "update");
        assertEquals(update.getEmail(), "update@user.com");

        UserDto userDto2 = new UserDto();
        userDto2.setName("name2");
        userDto2.setEmail("email2@user.com");

        service.save(userDto2);

        List<UserDto> all = service.getAll();

        assertEquals(all.size(), 2);
        assertEquals(all.get(0).getId(), 1L);
        assertEquals(all.get(0).getName(), "update");
        assertEquals(all.get(1).getName(), "name2");

        UserDto byId1 = service.getById(1L);

        assertEquals(byId1.getId(), 1L);
        assertEquals(byId1.getName(), "update");

        service.delete(1L);

        assertThrows(NotFoundException.class, () -> service.getById(1L));
    }
}
