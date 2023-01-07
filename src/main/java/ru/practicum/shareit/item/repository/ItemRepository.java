package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository {

    Item createItem(Item item, User user);

    Item updateItem(Item item, long userId, long itemId);

    Item getItemById(Long id);

    List<Item> getItemsByOwner(Long userId);

    List<Item> getItemsBySearch(String text);
}
