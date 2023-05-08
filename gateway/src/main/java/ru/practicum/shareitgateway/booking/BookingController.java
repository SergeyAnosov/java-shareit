package ru.practicum.shareitgateway.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.common.BookingStatusException;
import ru.practicum.shareitgateway.common.Create;
import ru.practicum.shareitgateway.booking.dto.BookingDtoShort;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@Validated(Create.class) @RequestBody BookingDtoShort bookingDtoShort,
                                         @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        return bookingClient.createBooking(bookingDtoShort, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam Boolean approved) {
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}") //Может быть выполнено либо автором бронирования, либо владельцем вещи
    public ResponseEntity<Object> findById(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllBookingsForUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(name = "state", defaultValue = "ALL", required = false) String state,
                                                @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                @Positive @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new BookingStatusException("Unknown state: " + state));
        return bookingClient.getBookingsByUser(userId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllForUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(name = "state", defaultValue = "ALL", required = false) String state,
                                                @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                                @Positive @RequestParam(required = false, defaultValue = "10") int size) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new BookingStatusException("Unknown state: " + state));
        return bookingClient.getBookingsByOwner(userId, bookingState, from, size);
    }
}
