package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

class BookingMappersTest {

    User user = new User(1L, "test", "email");

    ItemRequest expectedRequest = new ItemRequest(2L, "description", user, LocalDateTime.of(2023, 12, 12, 12, 12));

    //Item item = new Item(1L, "item", "description",)


    @Test
    void toBookingDto() {
    }

    @Test
    void toBooking() {
    }

    @Test
    void toBookingDtoShort() {
    }

    @Test
    void testToBooking() {
    }

    @Test
    void toBookingDtoResponse() {
    }
}