package ru.practicum.shareitserver.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    private Item item = new Item(1L, "name", "desc", true, new User(1L, "name", "email"), 1L);

    @Test
    void toItemDto() {
        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertEquals(1L, itemDto.getId());
        assertEquals("name", itemDto.getName());
        assertEquals("desc", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertEquals(1L, itemDto.getRequestId());
    }

    @Test
    void toItem() {
        ItemDto itemDto = new ItemDto(1L, "Название", "Описание", true, 1L);

        Item item = ItemMapper.toItem(itemDto, 1L);

        assertEquals(1L, item.getId());
        assertEquals("Название", item.getName());
        assertEquals("Описание", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(1L, item.getRequestId());
    }

    @Test
    void toItemInfoDto() {
        ItemInfoDto itemInfoDto = ItemMapper.toItemInfoDto(item);
        assertEquals(1L, itemInfoDto.getId());
        assertEquals("name", itemInfoDto.getName());
        assertEquals("desc", itemInfoDto.getDescription());
        assertTrue(itemInfoDto.getAvailable());
        assertNull(itemInfoDto.getLastBooking());
        assertNull(itemInfoDto.getNextBooking());
        assertNull(itemInfoDto.getComments());
    }
}