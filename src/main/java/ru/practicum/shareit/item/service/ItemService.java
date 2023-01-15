package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, long userId, UserDto userDto);

    ItemDto update(ItemDto itemDto, Long userId, long itemId);

    ItemDto getById(long itemId);

    List<ItemDto> getOwnerItems(long userId);

    List<ItemDto> getBySearch(String text);
}
