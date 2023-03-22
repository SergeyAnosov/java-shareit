package ru.practicum.shareitserver.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.request.model.ItemRequest;
import ru.practicum.shareitserver.request.repository.ItemRequestRepository;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private User firstUser;
    private User secondUser;
    private Item firstItem;
    private Item secondItem;
    private Item thirdItem;
    private ItemRequest firstRequest;
    private ItemRequest secondRequest;
    private PageRequest page;

    @BeforeEach
    void beforeEach() {
        firstUser = userRepository.save(new User(1L, "Имя первого", "first@email.com"));
        testEntityManager.persist(firstUser);
        secondUser = userRepository.save(new User(2L, "Имя второго", "second@email.com"));
        testEntityManager.persist(secondUser);
        firstRequest = itemRequestRepository.save(new ItemRequest(1L, "Реквест на первый и второй", firstUser, LocalDateTime.now()));
        testEntityManager.persist(firstRequest);
        secondRequest = itemRequestRepository.save(new ItemRequest(2L, "Реквест на третий", firstUser, LocalDateTime.now()));
        testEntityManager.persist(secondRequest);
        firstItem = itemRepository.save(new Item(1L, "Название первого", "Описание первого", true, firstUser, 1L));
        testEntityManager.persist(firstItem);
        secondItem = itemRepository.save(new Item(2L, "Название второго", "Описание второго", true, firstUser, 1L));
        testEntityManager.persist(secondItem);
        thirdItem = itemRepository.save(new Item(3L, "Название третьего", "Описание третьего", true, secondUser, 2L));
        testEntityManager.persist(thirdItem);
        testEntityManager.getEntityManager().getTransaction().commit();
        PageRequest page = PageRequest.of(0, 10);
    }

    @AfterEach
    public void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void getOwnersItems() {
        Page<Item> items = itemRepository.getOwnersItems(1L, page);

        assertEquals(2, items.getContent().size());
    }

    @Test
    void search() {
        Page<Item> items = itemRepository.search("вТоРоГо", page);

        assertEquals(1, items.getContent().size());
    }

    @Test
    void findByRequestId() {
        List<Item> items = itemRepository.findByRequestId(1L);

        assertEquals(2, items.size());
    }
}