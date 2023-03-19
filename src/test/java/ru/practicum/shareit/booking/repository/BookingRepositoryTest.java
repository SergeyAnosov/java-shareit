package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private User firstUser;
    private User secondUser;
    private Item firstItem;
    private Item secondItem;
    private Booking firstBooking;
    private Booking secondBooking;
    private PageRequest page;
    private LocalDateTime date;

    @BeforeEach
    public void beforeEach() {
        firstUser = userRepository.save(new User(1L, "Имя первого", "first@email.com"));
        entityManager.persist(firstUser);
        secondUser = userRepository.save(new User(2L, "Имя второго", "second@email.com"));
        entityManager.persist(secondUser);
        firstItem = itemRepository.save(new Item(
                        1L,
                        "Название первого",
                        "Описание первого",
                        true,
                        firstUser,
                        null
                )
        );
        entityManager.persist(firstItem);
        secondItem = itemRepository.save(new Item(
                        2L,
                        "Название второго",
                        "Описание второго",
                        true,
                        secondUser,
                        null
                )
        );
        entityManager.persist(secondItem);
        firstBooking = bookingRepository.save(new Booking(
                        1L,
                        LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                        LocalDateTime.of(2024, 1, 3, 1, 1, 1),
                        firstItem,
                        firstUser,
                        BookingStatus.WAITING
                )
        );
        entityManager.persist(firstBooking);
        secondBooking = bookingRepository.save(new Booking(
                        2L,
                        LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                        LocalDateTime.of(2024, 1, 3, 1, 1, 1),
                        secondItem,
                        firstUser,
                        BookingStatus.WAITING
                )
        );
        entityManager.persist(secondBooking);
        entityManager.getEntityManager().getTransaction().commit();
        page = PageRequest.of(0, 10);
        date = LocalDateTime.of(2024, 1, 2, 1, 1, 1);
    }

    @AfterEach
    public void afterEach() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void findAllByBooker_IdOrderByStartDesc() {
        Page<Booking> bookings = bookingRepository.findAllByBooker_IdOrderByStartDesc(1L, page);

        assertEquals(1L, bookings.getContent().get(0).getId());
        assertEquals(1L, bookings.getContent().get(0).getItem().getId());
        assertTrue(bookings.getContent().get(0).getEnd().isBefore(date.plusDays(2)));

    }

    @Test
    void findAllByBooker_IdAndEndIsBeforeOrderByEndDesc() {
        Page<Booking> bookings = bookingRepository.findAllByBooker_IdAndEndIsBeforeOrderByEndDesc(1L, date.plusDays(2), page);

        assertEquals(1L, bookings.getContent().get(0).getId());
        assertEquals(1L, bookings.getContent().get(0).getItem().getId());
        assertTrue(bookings.getContent().get(0).getEnd().isBefore(date.plusDays(2)));
    }

    @Test
    void findAllByBooker_IdAndEndIsAfterOrderByStartDesc() {
        Page<Booking> bookings = bookingRepository.findAllByBooker_IdAndEndIsAfterOrderByStartDesc(1L, date.minusDays(2), page);

        assertEquals(1L, bookings.getContent().get(0).getItem().getId());
        assertTrue(bookings.getContent().get(0).getStart().isAfter(date.minusDays(2)));
        assertEquals(BookingStatus.WAITING, bookings.getContent().get(0).getStatus());
    }

    @Test
    void findAllByBooker_IdAndStatusEqualsOrderByStartDesc() {
        Page<Booking> bookings = bookingRepository.findAllByBooker_IdAndStatusEqualsOrderByStartDesc(1L, BookingStatus.WAITING, page);

        assertEquals(1L, bookings.getContent().get(0).getItem().getId());
        assertEquals(BookingStatus.WAITING, bookings.getContent().get(0).getStatus());
    }

    @Test
    void findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(1L, date, date, page).getContent();

        assertEquals(2, bookings.size());
    }

    @Test
    void findAllByItem_Owner_IdOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(1L, page).getContent();

        assertEquals(1, bookings.size());
    }

    @Test
    void findAllByItem_Owner_IdAndEndIsBeforeOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(1L, date.plusDays(2), page).getContent();

        assertEquals(1, bookings.size());
    }

    @Test
    void findAllByItem_Owner_IdAndEndIsAfterOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItem_Owner_IdAndEndIsAfterOrderByStartDesc(2L, date.minusDays(2), page).getContent();

        assertEquals(1, bookings.size());
    }

    @Test
    void findAllByItem_Owner_IdAndStatusEqualsOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItem_Owner_IdAndStatusEqualsOrderByStartDesc(2L, BookingStatus.WAITING, page).getContent();

        assertEquals(1, bookings.size());
    }

    @Test
    void findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(2L, date, date, page).getContent();

        assertEquals(1, bookings.size());

    }

    @Test
    void findFirstByItem_IdAndStartIsBeforeAndStatusOrderByStartDesc() {
        Booking last = bookingRepository.findFirstByItem_IdAndStartIsBeforeAndStatusOrderByStartDesc(1L, date, BookingStatus.WAITING);

        assertEquals(1L, last.getBooker().getId());
        assertEquals(1L, last.getItem().getId());
        assertTrue(last.getEnd().isBefore(date.plusDays(2)));
    }
}