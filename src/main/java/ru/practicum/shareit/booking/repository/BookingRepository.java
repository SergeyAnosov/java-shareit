package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId, PageRequest pageRequest);

    //byBooker
    Page<Booking> findByBooker_IdAndEndIsBeforeOrderByEndDesc(Long bookerId, LocalDateTime time, PageRequest pageRequest); //PAST

    Page<Booking> findByBooker_IdAndEndIsAfterOrderByEndDesc(Long bookerId, LocalDateTime time, PageRequest pageRequest); //FUTURE

    Page<Booking> findByBooker_IdAndStatusEquals(Long bookerId, BookingStatus status, PageRequest pageRequest); //WAITING & REJECTED

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end, PageRequest pageRequest); //CURRENT

    //byItem
    Page<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long ownerId, PageRequest pageRequest);

    Page<Booking> findAllByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime time, PageRequest pageRequest); //PAST

    Page<Booking> findAllByItem_Owner_IdAndEndIsAfterOrderByStartDesc(Long ownerId, LocalDateTime time, PageRequest pageRequest); //FUTURE

    Page<Booking> findAllByItem_Owner_IdAndStatusEqualsOrderByStartDesc(Long ownerId, BookingStatus status, PageRequest pageRequest); //WAITING & REJECTED

    Page<Booking> findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime start, LocalDateTime end, PageRequest pageRequest); //CURRENT

    //for Last and Next Booking
    Booking findByItem_IdAndEndBeforeAndStatusNot(Long itemId, LocalDateTime end, BookingStatus status);

    Booking findByItem_IdAndStartAfterAndStatus(Long itemId, LocalDateTime start, BookingStatus status);

    //for comments
    List<Booking> findAllByBooker_IdAndItem_IdAndEndIsBefore(Long bookerId, Long itemId, LocalDateTime time);
}
