package ru.practicum.shareitserver.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareitserver.item.dto.ItemDto;
import ru.practicum.shareitserver.item.dto.ItemMapper;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.request.ItemRequestController;
import ru.practicum.shareitserver.request.dto.ItemRequestDto;
import ru.practicum.shareitserver.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareitserver.request.model.ItemRequest;
import ru.practicum.shareitserver.request.service.ItemRequestService;
import ru.practicum.shareitserver.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerIT {
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    private ItemRequestDto itemRequestDto;
    private ItemRequestWithItemsDto itemRequestWithItemsDto;

    @BeforeEach
    void beforeEach() {
        itemRequestDto = new ItemRequestDto(2L, "description", 1L, LocalDateTime.of(2023, 12, 12, 12, 12,12));
        User expectedUser = new User(1L, "test", "email");
        Item item1 = new Item(1L, "name1", "desc1", true, 1L);
        Item item2 = new Item(2L, "name2", "desc2", true, 2L);
        List<ItemDto> items = List.of(ItemMapper.toItemDto(item1), ItemMapper.toItemDto(item2));
        ItemRequest itemRequest = new ItemRequest(2L, "description", expectedUser, LocalDateTime.of(2023, 12, 12, 12, 12,12));
        itemRequestWithItemsDto = new ItemRequestWithItemsDto(2L, "description", LocalDateTime.of(2023, 12, 12, 12, 12,12), items);
    }

    @SneakyThrows
    @Test
    void create() {
        when(itemRequestService.save(any(), anyLong())).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().toString())));
    }

    @SneakyThrows
    @Test
    void getAllRequests() {
        when(itemRequestService.getAllRequests(Mockito.anyLong())).thenReturn(List.of(itemRequestWithItemsDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @SneakyThrows
    @Test
    void findRequestsWithFromAndSize() {
        when(itemRequestService.findAllRequestsByNotId(any(), anyLong())).thenReturn(List.of(itemRequestWithItemsDto));

        mockMvc.perform(get("/requests/all?from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @SneakyThrows
    @Test
    void findById() {
        when(itemRequestService.findById(anyLong(), anyLong())).thenReturn(itemRequestWithItemsDto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestWithItemsDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestWithItemsDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestWithItemsDto.getCreated().toString())));
    }
}