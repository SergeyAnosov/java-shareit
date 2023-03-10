package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId);

    //byBooker
    List<Booking> findByBooker_IdAndEndIsBeforeOrderByEndDesc(Long bookerId, LocalDateTime time); //PAST

    List<Booking> findByBooker_IdAndEndIsAfterOrderByEndDesc(Long bookerId, LocalDateTime time); //FUTURE

    List<Booking> findByBooker_IdAndStatusEquals(Long bookerId, BookingStatus status); //WAITING & REJECTED

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end); //CURRENT

    //byItem
    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime time); //PAST

    List<Booking> findAllByItem_Owner_IdAndEndIsAfterOrderByStartDesc(Long ownerId, LocalDateTime time); //FUTURE

    List<Booking> findAllByItem_Owner_IdAndStatusEqualsOrderByStartDesc(Long ownerId, BookingStatus status); //WAITING & REJECTED

    List<Booking> findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime start, LocalDateTime end); //CURRENT

    //for Last and Next Booking
    Booking findByItem_IdAndEndBeforeAndStatusNot(Long itemId, LocalDateTime end, BookingStatus status);

    Booking findByItem_IdAndStartAfterAndStatus(Long itemId, LocalDateTime start, BookingStatus status);

    //for comments
    List<Booking> findAllByBooker_IdAndItem_IdAndEndIsBefore(Long bookerId, Long itemId, LocalDateTime time);
}
