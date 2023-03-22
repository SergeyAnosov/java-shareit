package ru.practicum.shareit.mappers;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    User expectedUser = new User(1L, "test", "email");
    ItemRequest itemRequest = new ItemRequest(2L, "description", expectedUser, LocalDateTime.of(2023, 12, 12, 12, 12));
    ItemRequestDto itemRequestDto = new ItemRequestDto(2L, "description", 1L, LocalDateTime.of(2023, 12, 12, 12, 12));


    @Test
    void toDto() {
        ItemRequestDto response = ItemRequestMapper.INSTANCE.toDto(itemRequest);

        assertEquals(itemRequestDto.getId(), response.getId());
        assertEquals(itemRequestDto.getDescription(), response.getDescription());
        assertEquals(itemRequestDto.getCreated(), response.getCreated());
    }

    @Test
    void toItemRequest() {
        ItemRequest response = ItemRequestMapper.INSTANCE.toItemRequest(itemRequestDto);

        assertEquals(itemRequestDto.getId(), response.getId());
        assertEquals(itemRequestDto.getDescription(), response.getDescription());
        assertEquals(itemRequestDto.getCreated(), response.getCreated());
    }
}