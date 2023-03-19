package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingDtoShort;

import java.util.List;

public interface BookingService {
    BookingDtoResponse create(BookingDtoShort bookingDtoShort, Long bookerId);

    BookingDtoResponse findById(Long bookingId, Long userId);

    List<BookingDtoResponse> findAllByUser(String state, Long userId, int from, int size);

    BookingDtoResponse update(Long userId, Long bookingId, Boolean approved);

    List<BookingDtoResponse> findAllByOwner(String state, Long userId, int from, int size);
}
