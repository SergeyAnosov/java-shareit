package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemDto create(ItemDto itemDto, long userID, UserDto userDto) {
        Item item = ItemMapper.toItem(itemDto, userDto.getId());
        User user = UserMapper.toUser(userDto);
        return ItemMapper.toItemDto(itemRepository.create(item, user));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long userId, long itemId) {
        Item item = ItemMapper.toItem(itemDto, userId);
        return ItemMapper.toItemDto(itemRepository.update(item, userId, itemId));
    }

    @Override
    public ItemDto getById(long itemId) {
        return ItemMapper.toItemDto(itemRepository.getById(itemId).orElseThrow(() ->
                new EntityNotFoundException("такой вещи нет")));
    }

    @Override
    public List<ItemDto> getOwnerItems(long userId) {
        return itemRepository.getItemsByOwner(userId).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getBySearch(String text) {
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : itemRepository.getBySearch(text)) {
            itemsDto.add(ItemMapper.toItemDto(item));
        }
        return itemsDto;
    }
}
