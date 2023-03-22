package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDtoResponse create(@Validated(Create.class) @RequestBody BookingDtoShort bookingDtoShort,
                                     @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        return bookingService.create(bookingDtoShort, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse approveBooking(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam Boolean approved) {
        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}") //Может быть выполнено либо автором бронирования, либо владельцем вещи
    public BookingDtoResponse findById(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoResponse> findAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(name = "state", defaultValue = "ALL", required = false) String state,
                                                  @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                  @Positive @RequestParam(name = "size",required = false, defaultValue = "10") int size) {
        return bookingService.findAllByUser(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllForUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(name = "state", defaultValue = "ALL", required = false) String state,
                                                  @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                                  @Positive @RequestParam(required = false, defaultValue = "10") int size) {
        return bookingService.findAllByOwner(state, userId, from, size);
    }
}
