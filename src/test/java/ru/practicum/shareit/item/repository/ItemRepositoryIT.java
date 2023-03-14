package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class ItemRepositoryIT {

    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user1;
    private Item item1;
    private ItemRequest itemRequest1;


    @BeforeEach
    private void addItems() {
        user1 = userRepository.save(new User(1L, "name1", "email1@mail.ru"));
        itemRequest1 = itemRequestRepository.save(new ItemRequest(1L, "request1", user1, LocalDateTime.now()));
        item1 = itemRepository.save(new Item(1L, "item1", "description1", true, user1, 1L));
    }

    @Test
    void findByRequestId() {
        List<Item> actualItems = itemRepository.findByRequestId(1L);

        assertFalse(actualItems.isEmpty());
        assertEquals(actualItems.size(), 1);

        Item item = actualItems.get(0);
        assertEquals(1L, item.getId());
        assertEquals("item1", item.getName());
        assertEquals("description1", item.getDescription());
        assertEquals(true, item.getAvailable());

    }
    @AfterEach
    private void deleteItems() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }



}