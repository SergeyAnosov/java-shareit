package ru.practicum.shareit.mappers;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRequestWithItemsMapperTest {

    User expectedUser = new User(1L, "test", "email");
    Item item1 = new Item(1L, "name1", "desc1", true, 1L);
    Item item2 = new Item(2L, "name2", "desc2", true, 2L);
    List<ItemDto> items = List.of(ItemMapper.toItemDto(item1), ItemMapper.toItemDto(item2));
    ItemRequest itemRequest = new ItemRequest(2L, "description", expectedUser, LocalDateTime.of(2023, 12, 12, 12, 12));
    ItemRequestWithItemsDto itemRequestWithItemsDto =
            new ItemRequestWithItemsDto(2L, "description", LocalDateTime.of(2023, 12, 12, 12, 12), items);


    @Test
    void toDto() {
        ItemRequestWithItemsDto response = ItemRequestWithItemsMapper.INSTANCE.toDto(itemRequest);

        assertEquals(itemRequestWithItemsDto.getId(), response.getId());
        assertEquals(itemRequestWithItemsDto.getDescription(), response.getDescription());
        assertEquals(itemRequestWithItemsDto.getCreated(), response.getCreated());
    }
}