package ru.practicum.shareitserver.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareitserver.exception.NotFoundException;
import ru.practicum.shareitserver.item.ItemController;
import ru.practicum.shareitserver.item.dto.*;
import ru.practicum.shareitserver.item.model.Comment;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.item.service.ItemService;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTestIT {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    @MockBean
    private UserService userService;
    private Item correctItem;
    private Comment comment;
    private final NotFoundException notFoundException = new NotFoundException(HttpStatus.NOT_FOUND, "не найдено");


    @BeforeEach
    void beforeEach() {
        User user = new User(1L, "name", "email@ya.ru");
        correctItem = new Item(1L, "Вещь1", "Описание", true,
                user, 1L);
        comment = new Comment(1L, "Комментарий", correctItem, user, LocalDateTime.now());
    }

    @SneakyThrows
    @Test
    void create_success() {
        ItemDto correctDto = ItemMapper.toItemDto(correctItem);
        when(itemService.save(any(), any(), any())).thenReturn(correctDto);

        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(correctDto))
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(correctDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(correctDto.getName())))
                .andExpect(jsonPath("$.description", is(correctDto.getDescription())))
                .andExpect(jsonPath("$.available", is(correctDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(correctDto.getRequestId()), Long.class))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @SneakyThrows
    @Test
    void create_Fail_whenUserNotFound() {
        ItemDto correctDto = ItemMapper.toItemDto(correctItem);
        when(itemService.save(any(), any(), any())).thenThrow(notFoundException);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctDto))
                        .header("X-Sharer-User-Id", 100))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void update_success() {
        ItemDto correctDto = ItemMapper.toItemDto(correctItem);
        when(itemService.update(any(), anyLong(),anyLong()))
                .thenReturn(correctDto);

        mockMvc.perform(patch("/items/1")
                .content(objectMapper.writeValueAsString(correctDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(correctDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(correctDto.getName())))
                .andExpect(jsonPath("$.description", is(correctDto.getDescription())));
    }

    @SneakyThrows
    @Test
    void getById() {
        ItemInfoDto correctDto = ItemMapper.toItemInfoDto(correctItem);
        when(itemService.getById(anyLong(), anyLong()))
                .thenReturn(correctDto);

        mockMvc.perform(get("/items/1")
                .content(objectMapper.writeValueAsString(correctDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(correctDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(correctDto.getName())))
                .andExpect(jsonPath("$.description", is(correctDto.getDescription())));

    }

    @SneakyThrows
    @Test
    void getOwnerItems() {
        when(itemService.getOwnerItems(anyInt(), anyInt(), anyLong()))
                .thenReturn(List.of(ItemMapper.toItemInfoDto(correctItem)));

        mockMvc.perform(get("/items?from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @SneakyThrows
    @Test
    void getBySearch_Success_whenText() {
        when(itemService.search(any(), anyString()))
                .thenReturn(List.of(ItemMapper.toItemDto(correctItem)));

        mockMvc.perform(get("/items/search?text=предмет&from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @SneakyThrows
    @Test
    void getBySearch_Fail_whenTextIsBlank() {
        when(itemService.search(any(), eq("")))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items/search?text=&from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @SneakyThrows
    @Test
    void createComment() {
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        when(itemService.createComment(any(), anyLong(), anyLong()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.item.id", is(commentDto.getItem().getId()), Long.class));
    }
}