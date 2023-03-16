package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    private User user;
    private ItemRequest firstRequest;
    private ItemRequest secondRequest;

    @BeforeEach
    public void beforeEach() {
        user = userRepository.save(new User(1L, "Имя", "email@email.com"));
        entityManager.persist(user);
        firstRequest = itemRequestRepository.save(new ItemRequest(1L, "Описание первого", user, LocalDateTime.now()));
        entityManager.persist(firstRequest);
        secondRequest = itemRequestRepository.save(new ItemRequest(2L, "Описание второго", user, LocalDateTime.now()));
        entityManager.persist(secondRequest);
        entityManager.getEntityManager().getTransaction().commit();
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void findAllByRequesterIdOrderByCreated() {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreated(1L);

        assertEquals(2, requests.size());
    }

    @Test
    void findAllByRequesterIdNotOrderByCreatedDesc() {
        List<ItemRequest> content = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(PageRequest.of(0, 10), 1L).getContent();

        assertEquals(0, content.size());
    }
}