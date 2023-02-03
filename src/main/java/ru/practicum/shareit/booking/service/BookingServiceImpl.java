package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongUserException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

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

        Booking booking = BookingMapper.toBooking(bookingDtoShort);


        if (!item.getAvailable()) {
            throw new NotFoundException(HttpStatus.BAD_REQUEST, "вещь не доступна для бронирования");
        }
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);

        booking.setItem(item);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDtoResponse(booking);
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
        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public BookingDtoResponse findById(Long bookingId, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND,
                "пользователя не существует"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND,
                "бронирования с таким id не существует"));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return BookingMapper.toBookingDtoResponse(booking);
        } else {
            throw new WrongUserException("Может быть выполнено либо автором бронирования, либо владельцем вещи");
        }
    }

    @Override
    public List<BookingDtoResponse> findAllByUser(String state, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND,
                "пользователя не существует"));
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByUser(userId).stream()
                        .map(BookingMapper::toBookingDtoResponse).collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findByBooker_IdAndEndIsBeforeOrderByEndDesc(userId, LocalDateTime.now())
                        .stream().map(BookingMapper::toBookingDtoResponse).collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findByBooker_IdAndEndIsAfterOrderByEndDesc(userId, LocalDateTime.now())
                        .stream().map(BookingMapper::toBookingDtoResponse).collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findByBooker_IdAndStatusEquals(userId, BookingStatus.WAITING)
                        .stream().map(BookingMapper::toBookingDtoResponse).collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findByBooker_IdAndStatusEquals(userId, BookingStatus.REJECTED)
                        .stream().map(BookingMapper::toBookingDtoResponse).collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now())
                        .stream().map(BookingMapper::toBookingDtoResponse).collect(Collectors.toList());
            default:
                throw new BookingStatusException("Unknown state: " + state);
        }
    }

    @Override
    public List<BookingDtoResponse> findAllByOwner(String state, Long ownerId) {
        userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND,
                "пользователя не существует"));
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(ownerId).stream()
                        .map(BookingMapper::toBookingDtoResponse).collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findAllByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(ownerId, LocalDateTime.now())
                        .stream().map(BookingMapper::toBookingDtoResponse).collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findAllByItem_Owner_IdAndEndIsAfterOrderByStartDesc(ownerId, LocalDateTime.now())
                        .stream().map(BookingMapper::toBookingDtoResponse).collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findAllByItem_Owner_IdAndStatusEqualsOrderByStartDesc(ownerId, BookingStatus.WAITING)
                        .stream().map(BookingMapper::toBookingDtoResponse).collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findAllByItem_Owner_IdAndStatusEqualsOrderByStartDesc(ownerId, BookingStatus.REJECTED)
                        .stream().map(BookingMapper::toBookingDtoResponse).collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId,
                                LocalDateTime.now(), LocalDateTime.now())
                        .stream().map(BookingMapper::toBookingDtoResponse).collect(Collectors.toList());
            default:
                throw new BookingStatusException("Unknown state: " + state);
        }
    }

    private void checkItemBelongUser(Long itemId, Long userId) {
       if (!itemRepository.findById(itemId).get().getOwner().getId().equals(userId)) {
           throw new WrongUserException("поддтверждать бронирование может только собственник");
       }
    }
}
