package ru.practicum.shareitserver.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ru.practicum.shareitserver.exception.EmailExistException;
import ru.practicum.shareitserver.exception.EntityNotFoundException;
import ru.practicum.shareitserver.exception.NotFoundException;
import ru.practicum.shareitserver.user.dto.UserDto;
import ru.practicum.shareitserver.user.dto.UserMapper;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;
    Long userId = 1L;
    User oldUser;
    User secondUser;
    UserDto userDto;

    @BeforeEach
    public void beforeEach() {
        oldUser = new User(1L, "name", "email");
        secondUser = new User(2L, "name2", "email2");
    }

    @Test
    void save() {
        when(userRepository.save(any())).thenReturn(oldUser);
        UserDto userDto = UserMapper.toUserDto(oldUser);

        UserDto savedDto = userService.save(userDto);

        assertEquals(userDto.getId(), savedDto.getId());
        assertEquals(userDto.getName(), savedDto.getName());
        assertEquals(userDto.getEmail(), savedDto.getEmail());

        verify(userRepository).save(any());
    }

    @Test
    public void saveUserWhenEmailExistTestFail() {
        when(userRepository.save(any())).thenThrow(new EmailExistException("Этот email уже существует"));

        assertThrows(EmailExistException.class, () -> userService.save(UserMapper.toUserDto(oldUser)));

        verify(userRepository, never()).save(oldUser);
    }

    @Test
    void updateUser_whenUserFound_andSetAvailableFields() {
        User newUser = new User(1L, "newName", "newEmail");
        UserDto forUpdate = UserMapper.toUserDto(newUser);
        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));

        userService.update(forUpdate, userId);

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        assertEquals("newName", savedUser.getName());
        assertEquals("newEmail", savedUser.getEmail());
    }

    @Test
    void updateUser_whenUserFound_NewNameIsNull_andSaved() {
        User newUser = new User(1L, "", "newEmail");
        UserDto forUpdate = UserMapper.toUserDto(newUser);
        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));

        userService.update(forUpdate, userId);

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        assertEquals("name", savedUser.getName());
        assertEquals("newEmail", savedUser.getEmail());
    }

    @Test
    void updateUser_whenUserFound_NewEmailIsNull_andSaved() {
        User newUser = new User(1L, "newName", "");
        UserDto forUpdate = UserMapper.toUserDto(newUser);
        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));

        userService.update(forUpdate, userId);

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        assertEquals("newName", savedUser.getName());
        assertEquals("email", savedUser.getEmail());
    }

    @Test
    void updateUser_whenUserNotFound_ExceptionThrown() {
        doThrow(EntityNotFoundException.class).when(userRepository).findById(Mockito.anyLong());

        assertThrows(EntityNotFoundException.class,
                () -> userService.update(userDto, userId));
        verify(userRepository, never()).save(Mockito.any());
    }


    @Test
    void getAll() {
        when(userRepository.findAll()).thenReturn(List.of(oldUser, secondUser));
        List<UserDto> all = userService.getAll();

        verify(userRepository).findAll();

        Assertions.assertAll(
                () -> Assertions.assertEquals(all.size(), 2),
                () -> Assertions.assertEquals(all.get(0), UserMapper.toUserDto(oldUser)),
                () -> Assertions.assertEquals(all.get(1), UserMapper.toUserDto(secondUser))
        );
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById((anyLong()))).thenReturn(Optional.of(oldUser));

        userService.delete(1L);

        verify(userRepository).delete(any());
        assertDoesNotThrow(() -> userService.delete(1L));
    }

    @Test
    void deleteUser_whenUserNotFound() {
        when(userRepository.findById((anyLong()))).thenThrow(new NotFoundException(HttpStatus.NOT_FOUND, "пользователь не найден"));

        verify(userRepository, never()).delete(any());

        assertThrows(NotFoundException.class, () -> userService.delete(Mockito.anyLong()));
    }

    @Test
    void getById_userExist() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(oldUser));

        UserDto userById = userService.getById(1L);

        verify(userRepository).findById(anyLong());

        Assertions.assertEquals(userById.getId(), oldUser.getId());
        Assertions.assertEquals(userById.getName(), oldUser.getName());
        Assertions.assertEquals(userById.getEmail(), oldUser.getEmail());
    }

    @Test
    void getByID_whenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(1L));
    }
}