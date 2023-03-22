package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EmailExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerIT {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


    private User correctUser;

    private User userWithWrongName;

    private User userWithWrongEmail;

    private final EmailExistException emailExistException = new EmailExistException("email уже существует");
    private final NotFoundException userNotFoundException = new NotFoundException(HttpStatus.NOT_FOUND, "пользователь не найден");

    @BeforeEach
    public void beforeEach() {
        correctUser = new User(1L, "name", "anosov@mail.ru");
        userWithWrongName = new User(2L, "", "anosov2@mail.ru");
        userWithWrongEmail = new User(3L, "name3", "mail.ru");
    }


    @SneakyThrows
    @Test
    void createUSer_andSuccess() {
        UserDto correctDto = UserMapper.toUserDto(correctUser);
        when(userService.save(any())).thenReturn(correctDto);

       mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(correctUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(correctUser.getName())))
                .andExpect(jsonPath("$.email", is(correctUser.getEmail())))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @SneakyThrows
    @Test
    void createUser_whenUserIsNotValid() {
        UserDto notCorrectDto = UserMapper.toUserDto(userWithWrongName);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notCorrectDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).save(notCorrectDto);

        UserDto notCorrectDto1 = UserMapper.toUserDto(userWithWrongEmail);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notCorrectDto1)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).save(notCorrectDto1);
    }

    @SneakyThrows
    @Test
    void createUser_whenEmailAlreadyExist_andThrownException() {
        when(userService.save(any())).thenThrow(emailExistException);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).save(any());
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserDtoIsNotValid_thenReturnedBadRequest() {
        long userId = 3L;
        UserDto userDtoToUpdate = UserMapper.toUserDto(userWithWrongEmail);

        mockMvc.perform(patch("/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDtoToUpdate)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).update(userDtoToUpdate, userId);
    }

    @SneakyThrows
    @Test
    void updateUserFail_withDuplicateMail() {
        when(userService.update(any(), anyLong())).thenThrow(emailExistException);

        mockMvc.perform(patch("/users/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).update(any(), anyLong());
    }

    @SneakyThrows
    @Test
    void getAll() {
        when(userService.getAll()).thenReturn(List.of(UserMapper.toUserDto(correctUser)));
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void getById_success() {
        long userId = 1L;

        mockMvc.perform(get("/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).getById(userId);
    }

    @SneakyThrows
    @Test
    void getById_whenUserNotExist_andExceptionThrown() {
        when(userService.getById(Mockito.anyLong())).thenThrow(userNotFoundException);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void delete_success() {
        Mockito.doNothing().when(userService).delete(Mockito.anyLong());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService).delete(anyLong());
    }

    @SneakyThrows
    @Test
    void delete_whenUserNotFound() {
        Mockito.doThrow(userNotFoundException).when(userService).delete(Mockito.anyLong());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNotFound());
    }
}