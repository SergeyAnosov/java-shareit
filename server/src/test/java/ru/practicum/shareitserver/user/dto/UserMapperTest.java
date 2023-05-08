package ru.practicum.shareitserver.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareitserver.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    User user = new User(1L, "name", "email");

    UserDto userDto = new UserDto(4L, "nameDtr", "emailDto");

    @Test
    void toUserDto() {
        UserDto checkDto = UserMapper.toUserDto(user);

        assertEquals(checkDto.getId(), user.getId());
        assertEquals(checkDto.getName(), user.getName());
        assertEquals(checkDto.getEmail(), user.getEmail());
    }

    @Test
    void toUser() {
        User newUser = UserMapper.toUser(userDto);

        assertEquals(userDto.getId(), newUser.getId());
        assertEquals(userDto.getName(), newUser.getName());
        assertEquals(userDto.getEmail(), newUser.getEmail());
    }
}