package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMappersTest {
    private User booker = new User(1L, "name", "mail");
    private Booking booking = new Booking(
            1L,
            LocalDateTime.of(2024, 1, 1, 1, 1, 1),
            LocalDateTime.of(2024, 1, 2, 1, 1, 1),
            new Item(1L, "Корректный предмет", "Корректное описание", true, booker, 1L),
            booker,
            BookingStatus.WAITING);

    @Test
    void toBookingDto() {
        BookingDto bookingDto = BookingMappers.toBookingDto(booking);

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getItem().getId(), bookingDto.getItemId());
        assertEquals(booking.getBooker().getId(), bookingDto.getBookerId());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
    }

    @Test
    void toBooking() {
        BookingDtoShort bookingDto = new BookingDtoShort(
                1L,
                LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                LocalDateTime.of(2024, 1, 2, 1, 1, 1),
                1L
        );

        Booking booking1 = BookingMappers.toBooking(bookingDto);

        assertEquals(bookingDto.getId(), booking1.getId());
        assertEquals(bookingDto.getStart(), booking1.getStart());
        assertEquals(bookingDto.getEnd(), booking1.getEnd());
    }

    @Test
    void toBookingDtoShort() {
        BookingDtoShort bookingDto = BookingMappers.toBookingDtoShort(booking);

        assertEquals(bookingDto.getId(), booking.getId());
        assertEquals(bookingDto.getStart(), booking.getStart());
        assertEquals(bookingDto.getEnd(), booking.getEnd());
        assertEquals(bookingDto.getItemId(), booking.getItem().getId());

    }

    @Test
    void testToBookingDtoResponse() {
        BookingDtoResponse bookingDto = BookingMappers.toBookingDtoResponse(booking);

        assertEquals(bookingDto.getId(), booking.getId());
        assertEquals(bookingDto.getStart(), booking.getStart());
        assertEquals(bookingDto.getEnd(), booking.getEnd());
        assertEquals(bookingDto.getItem(), booking.getItem());
        assertEquals(bookingDto.getBooker(), booking.getBooker());
    }
}