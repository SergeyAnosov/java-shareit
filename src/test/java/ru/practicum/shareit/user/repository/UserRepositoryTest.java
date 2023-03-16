package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    public void beforeEach() {
        user = userRepository.save(new User(1L, "Имя", "email@email.com"));
        entityManager.persist(user);
        entityManager.getEntityManager().getTransaction().commit();

        Optional<User> byId = userRepository.findById(1L);

        assertTrue(byId.isPresent());
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    public void findBy() {
        Optional<User> byId = userRepository.findById(1L);

        assertTrue(byId.isPresent());
    }
}
