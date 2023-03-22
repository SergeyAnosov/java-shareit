package ru.practicum.shareitserver.item.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareitserver.item.dto.CommentDto;
import ru.practicum.shareitserver.item.dto.CommentDtoShort;
import ru.practicum.shareitserver.item.dto.ItemDto;
import ru.practicum.shareitserver.item.dto.ItemInfoDto;
import ru.practicum.shareitserver.user.dto.UserDto;

import java.util.List;

public interface ItemService {
    ItemDto save(ItemDto itemDto, Long userId, UserDto userDto);

    ItemDto update(ItemDto itemDto, Long userId, Long itemId);

    ItemInfoDto getById(Long itemId, Long userId);

    List<ItemInfoDto> getOwnerItems(int from, int size, Long userId);

    List<ItemDto> search(PageRequest pageRequest, String text);

    CommentDto createComment(CommentDtoShort commentDtoShort, Long authorId, Long itemId);
}
