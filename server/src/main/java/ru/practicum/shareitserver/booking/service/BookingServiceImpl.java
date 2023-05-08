package ru.practicum.shareitserver.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareitserver.booking.BookingStatus;
import ru.practicum.shareitserver.booking.dto.BookingDtoResponse;
import ru.practicum.shareitserver.booking.dto.BookingDtoShort;
import ru.practicum.shareitserver.booking.dto.BookingMappers;
import ru.practicum.shareitserver.booking.model.Booking;
import ru.practicum.shareitserver.booking.repository.BookingRepository;
import ru.practicum.shareitserver.exception.BookingStatusException;
import ru.practicum.shareitserver.exception.NotFoundException;
import ru.practicum.shareitserver.exception.WrongUserException;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.item.repository.ItemRepository;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.repository.UserRepository;
import ru.practicum.shareitserver.util.PageableMaker;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDtoResponse create(BookingDtoShort bookingDtoShort, Long bookerId) {
        Item item = itemRepository.findById(bookingDtoShort.getItemId()).orElseThrow(() ->
                new NotFoundException(HttpStatus.NOT_FOUND, "Предмет с таким id не найден"));
        User booker = userRepository.findById(bookerId).orElseThrow(() ->
                new NotFoundException(HttpStatus.NOT_FOUND, "такого юзера нет"));

        if (!bookingDtoShort.getStart().isBefore(bookingDtoShort.getEnd()) ||
                bookingDtoShort.getStart().isBefore(LocalDateTime.now())) {
            throw new NotFoundException(HttpStatus.BAD_REQUEST, "ошибка Start или End Date");
        }

        if (item.getOwner().getId().equals(bookerId)) {
            throw new WrongUserException("вещь принадлежит владельцу");
        }

        Booking booking = BookingMappers.toBooking(bookingDtoShort);


        if (!item.getAvailable()) {
            throw new NotFoundException(HttpStatus.BAD_REQUEST, "вещь не доступна для бронирования");
        }
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);

        booking.setItem(item);
        bookingRepository.save(booking);
        return BookingMappers.toBookingDtoResponse(booking);
    }

    @Override
    @Transactional
    public BookingDtoResponse update(Long userId, Long bookingId, Boolean approved) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(HttpStatus.BAD_REQUEST,
                "пользователя не существует"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "нет такого бронирования"));
        checkItemBelongUser(booking.getItem().getId(), userId);
        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new BookingStatusException("Бронирование уже одобрено");
        }
        if (!approved) {
            booking.setStatus(BookingStatus.REJECTED);
        } else {
            booking.setStatus(BookingStatus.APPROVED);
        }
        bookingRepository.save(booking);
        return BookingMappers.toBookingDtoResponse(booking);
    }

    @Override
    public BookingDtoResponse findById(Long bookingId, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND,
                "пользователя не существует"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND,
                "бронирования с таким id не существует"));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return BookingMappers.toBookingDtoResponse(booking);
        } else {
            throw new WrongUserException("Может быть выполнено либо автором бронирования, либо владельцем вещи");
        }
    }

    @Override
    public List<BookingDtoResponse> findAllByUser(String state, Long userId, int from, int size) {
        if (from < 0 || size < 0) {
            throw new NotFoundException(HttpStatus.BAD_REQUEST, "не верные параметры");
        }
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND,
                "пользователя не существует"));

        Pageable pageable = PageableMaker.makePage(from, size);
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByBooker_IdOrderByStartDesc(userId, pageable).stream()
                        .map(BookingMappers::toBookingDtoResponse).collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable)
                        .stream().map(BookingMappers::toBookingDtoResponse).collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findAllByBooker_IdAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable)
                        .stream().map(BookingMappers::toBookingDtoResponse).collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findAllByBooker_IdAndStatusEqualsOrderByStartDesc(userId, BookingStatus.WAITING, pageable)
                        .stream().map(BookingMappers::toBookingDtoResponse).collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findAllByBooker_IdAndStatusEqualsOrderByStartDesc(userId, BookingStatus.REJECTED, pageable)
                        .stream().map(BookingMappers::toBookingDtoResponse).collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findAllByBooker_IdAndAndStartBeforeAndEndAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), pageable)
                        .stream().map(BookingMappers::toBookingDtoResponse).collect(Collectors.toList());
            default:
                throw new BookingStatusException("Unknown state: " + state);
        }
    }

    @Override
    public List<BookingDtoResponse> findAllByOwner(String state, Long userId, int from, int size) {
        if (from < 0 || size < 0) {
            throw new NotFoundException(HttpStatus.BAD_REQUEST, "не верные параметры");
        }
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND,
                "пользователя не существует"));
        Pageable pageable = PageableMaker.makePage(from, size);
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(userId, pageable).stream()
                        .map(BookingMappers::toBookingDtoResponse).collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findAllByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable)
                        .stream().map(BookingMappers::toBookingDtoResponse).collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findAllByItem_Owner_IdAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable)
                        .stream().map(BookingMappers::toBookingDtoResponse).collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findAllByItem_Owner_IdAndStatusEqualsOrderByStartDesc(userId, BookingStatus.WAITING, pageable)
                        .stream().map(BookingMappers::toBookingDtoResponse).collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findAllByItem_Owner_IdAndStatusEqualsOrderByStartDesc(userId, BookingStatus.REJECTED, pageable)
                        .stream().map(BookingMappers::toBookingDtoResponse).collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                                LocalDateTime.now(), LocalDateTime.now(), pageable)
                        .stream().map(BookingMappers::toBookingDtoResponse).collect(Collectors.toList());
            default:
                throw new BookingStatusException("Unknown state: " + state);
        }
    }

    private void checkItemBelongUser(Long itemId, Long userId) {
        if (itemRepository.findById(itemId).isPresent()) {
            if (!itemRepository.findById(itemId).get()
                    .getOwner().getId().equals(userId)) {
                throw new WrongUserException("подтверждать бронирование может только собственник");
            }
        }
    }
}
