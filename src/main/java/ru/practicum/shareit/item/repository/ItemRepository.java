package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository {

    Item create(Item item, User user);

    Item update(Item item, long userId, long itemId);

    Item getById(Long id);

    List<Item> getItemsByOwner(Long userId);

    List<Item> getBySearch(String text);
}
