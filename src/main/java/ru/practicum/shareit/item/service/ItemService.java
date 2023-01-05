package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, UserDto userDto);

    ItemDto updateItem(ItemDto itemDto, Long userId, long itemId);

    ItemDto getItemById(long itemId);

    List<ItemDto> getOwnerItems(long userId);

    List<ItemDto> getItemsBySearch(String text);
}
