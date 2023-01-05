package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, UserDto userDto) {
        Item item = ItemMapper.toItem(itemDto, userDto.getId());
        User user = UserMapper.toUser(userDto);
        return ItemMapper.toItemDto(itemRepository.createItem(item, user));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long userId, long itemId) {
        Item item = ItemMapper.toItem(itemDto, userId);
        return ItemMapper.toItemDto(itemRepository.updateItem(item, userId, itemId));
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return ItemMapper.toItemDto(itemRepository.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getOwnerItems(long userId) {
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : itemRepository.getItemsByOwner(userId)) {
            itemsDto.add(ItemMapper.toItemDto(item));
        }
        return itemsDto;
    }

    @Override
    public List<ItemDto> getItemsBySearch(String text) {
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : itemRepository.getItemsBySearch(text)) {
            itemsDto.add(ItemMapper.toItemDto(item));
        }
        return itemsDto;
    }
}
