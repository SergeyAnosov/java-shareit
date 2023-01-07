package ru.practicum.shareit.item.repository;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@Repository
public class ItemRepositoryInMemory implements ItemRepository {
    private Map<Long, Item> items = new HashMap<>();
    private long id = 1;
    private UserRepository userRepository;

    @Autowired
    ItemRepositoryInMemory(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Item createItem(Item item, User user) {
        item.setId(id);
        id++;
        checkUser(user.getId());
        item.setOwner(user);
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item updateItem(Item newItem, long userId, long itemId) {
        checkItem(itemId);
        checkUser(userId);
        checkUserId(itemId, userId);
        Item item = items.get(itemId);
        if (newItem.getName() != null) {
            item.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            item.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            item.setAvailable(newItem.getAvailable());
        }
        return items.get(itemId);
    }

    @Override
    public Item getItemById(Long id) {
        if (items.containsKey(id)) {
            return items.get(id);
        } else {
            throw new EntityNotFoundException("Такой вещи нет");
        }
    }

    @Override
    public List<Item> getItemsByOwner(Long userId) {
        checkUser(userId);
        return items.values().stream().filter(item ->
                item.getOwner().getId().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public List<Item> getItemsBySearch(String text) {
        String textToLower = text.toLowerCase();
        List<Item> searchItems = new ArrayList<>();
        if (!text.isBlank()) {
            searchItems = items.values()
                    .stream()
                    .filter(item -> item.getAvailable() && (item.getName().toLowerCase().contains(textToLower)
                            || item.getDescription().toLowerCase().contains(textToLower)))
                    .collect(Collectors.toList());
        }
        return searchItems;
    }

    private void checkUser(long userId) {
        if (userRepository.getById(userId) == null) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь предмета не найден");
        }
    }

    private void checkItem(Long itemId) {
        if (items.get(itemId) == null) {
            throw new EntityNotFoundException("Предмет с таким id не найден");
        }
    }

    private Boolean checkUserId(Long itemId, Long userId) {
        if (items.get(itemId).getOwner().getId().equals(userId)) {
            return true;
        } else {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Редактировать вещь может только её владелец");
        }
    }
}
