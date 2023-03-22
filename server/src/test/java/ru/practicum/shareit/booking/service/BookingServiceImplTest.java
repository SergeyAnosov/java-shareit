package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingMappers;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongUserException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceImplTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    private User firstUser;
    private User secondUser;
    private Item item;
    private Booking booking;
    private final NotFoundException notFoundException = new NotFoundException(HttpStatus.NOT_FOUND, "Сущность не найдена");

    @BeforeEach
    public void beforeEach() {
        firstUser = new User(1L, "Имя первого", "first@email.com");
        secondUser = new User(2L, "Имя второго", "second@email.com");
        item = new Item(1L, "Название", "Описание", true, firstUser, 1L);
        booking = new Booking(
                1L,
                LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                LocalDateTime.of(2024, 1, 2, 1, 1, 1),
                item,
                firstUser,
                BookingStatus.APPROVED
        );
    }

    @Test
    void create_success() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        bookingService.create(BookingMappers.toBookingDtoShort(booking), 2L);

        Optional<Booking> createBooking = bookingRepository.findById(1L);

        assertEquals(booking.getId(), createBooking.get().getId());
        assertEquals(booking.getStart(), createBooking.get().getStart());
        assertEquals(booking.getEnd(), createBooking.get().getEnd());
        assertEquals(booking.getItem(), createBooking.get().getItem());
        assertEquals(booking.getBooker(), createBooking.get().getBooker());
        assertEquals(booking.getStatus(), createBooking.get().getStatus());
    }

    @Test
    void create_Fail_NoItem() {
        when(itemRepository.findById(anyLong())).thenThrow(notFoundException);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.create(BookingMappers.toBookingDtoShort(booking), 2L));
    }

    @Test
    void create_Fail_NoUser() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenThrow(notFoundException);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.create(BookingMappers.toBookingDtoShort(booking), 2L));
    }

    @Test
    void create_Fail_ItemBelongUser() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(WrongUserException.class, () -> bookingService.create(BookingMappers.toBookingDtoShort(booking), 1L));
    }

    @Test
    void create_Fail_itemNotAvailable() {
        Item item2 = new Item(2L, "Название2", "Описание2", false, firstUser, 1L);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item2));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.create(BookingMappers.toBookingDtoShort(booking), 2L));
    }

    @Test
    void create_Fail_WrongStartAndEnd() {
        Booking booking2 = new Booking(
                2L,
                LocalDateTime.of(2023, 1, 1, 1, 1, 1),
                LocalDateTime.of(2022, 1, 2, 1, 1, 1),
                item,
                firstUser,
                BookingStatus.APPROVED);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking2));

        assertThrows(NotFoundException.class, () -> bookingService.create(BookingMappers.toBookingDtoShort(booking2), 1L));
    }


    @Test
    void update_Fail_alreadyApproved() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(secondUser));
        when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

        assertThrows(BookingStatusException.class, () -> bookingService.update(1L, 2L, false));

       verify(bookingRepository, never()).save(booking);
    }

    @Test
    void update_Success_Approved() {
        Booking booking2 = new Booking(
                2L,
                LocalDateTime.of(2023, 1, 1, 1, 1, 1),
                LocalDateTime.of(2024, 1, 2, 1, 1, 1),
                item,
                firstUser,
                BookingStatus.WAITING);
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(secondUser));
        when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking2));

        BookingDtoResponse response = bookingService.update(1L, 2L, true);

        assertEquals(BookingStatus.APPROVED, response.getStatus());
    }

    @Test
    void update_Success_Rejected() {
        Booking booking2 = new Booking(
                2L,
                LocalDateTime.of(2023, 1, 1, 1, 1, 1),
                LocalDateTime.of(2024, 1, 2, 1, 1, 1),
                item,
                firstUser,
                BookingStatus.WAITING);
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(secondUser));
        when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking2));

        BookingDtoResponse response = bookingService.update(1L, 2L, false);

        assertEquals(BookingStatus.REJECTED, response.getStatus());
    }

    @Test
    void findById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));
        when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

        BookingDtoResponse responseDto = bookingService.findById(1L, 1L);
        assertEquals(1L, bookingService.findById(1L, 1L).getId());
    }

    @Test
    void findById_Fail() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));
        when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

        assertThrows(WrongUserException.class, () -> bookingService.findById(1L, 2L));
    }

    @Test
    void findById_Fail_UserNotFound() {
        when(userRepository.findById(anyLong())).thenThrow(notFoundException);
        when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.findById(1L, 2L));
    }

    @Test
    void findById_Fail_BookingNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));
        when(bookingRepository.findById(Mockito.anyLong())).thenThrow(notFoundException);

        assertThrows(NotFoundException.class, () -> bookingService.findById(1L, 2L));
    }

    @Test
    void findAllByUser_Fail_fromOrSizeIsNegative() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));

        assertThrows(NotFoundException.class,
                () -> bookingService.findAllByUser("ALL", 1L, -1, -2));
    }

    @Test
    void findAllByUser_Fail_UserNotFound() {
        when(userRepository.findById(anyLong())).thenThrow(notFoundException);

        assertThrows(NotFoundException.class,
                () -> bookingService.findAllByUser("ALL", 1L, -1, -2));
    }

    @Test
    void findAllByUser_Fail_Unsupported() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));

        assertThrows(BookingStatusException.class,
                () -> bookingService.findAllByUser("asdfns", 1L, 0, 10));
    }

    @Test
    void findAllByUser_Success_ALL() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(secondUser));
        when(bookingRepository.findAllByBooker_IdOrderByStartDesc(anyLong(), any())).thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDtoResponse> all = bookingService.findAllByUser("ALL", 2L, 0, 10);

        assertEquals(1, all.size());
    }

    @Test
    void findAllByOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(secondUser));
        when(bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(Mockito.anyLong(), any())).thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDtoResponse> all = bookingService.findAllByOwner("ALL", 2L, 0, 10);

        assertEquals(1, all.size());
    }

    @Test
    void findAllByOwner_Fail_Unsupported() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));

        assertThrows(BookingStatusException.class,
                () -> bookingService.findAllByOwner("asdfns", 1L, 0, 10));
    }
}