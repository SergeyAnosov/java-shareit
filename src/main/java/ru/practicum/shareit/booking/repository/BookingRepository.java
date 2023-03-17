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

    Page<Booking> findAllByBooker_IdAndEndIsBeforeOrderByEndDesc(Long bookerId, LocalDateTime time, PageRequest pageRequest); //PAST

    Page<Booking> findAllByBooker_IdAndEndIsAfterOrderByStartDesc(Long bookerId, LocalDateTime time, PageRequest pageRequest); //FUTURE

    Page<Booking> findAllByBooker_IdAndStatusEqualsOrderByStartDesc(Long bookerId, BookingStatus status, PageRequest pageRequest); //WAITING & REJECTED

    Page<Booking> findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end, PageRequest pageRequest); //CURRENT

    Page<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long ownerId, PageRequest pageRequest);

    Page<Booking> findAllByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime time, PageRequest pageRequest); //PAST

    Page<Booking> findAllByItem_Owner_IdAndEndIsAfterOrderByStartDesc(Long ownerId, LocalDateTime time, PageRequest pageRequest); //FUTURE

    Page<Booking> findAllByItem_Owner_IdAndStatusEqualsOrderByStartDesc(Long ownerId, BookingStatus status, PageRequest pageRequest); //WAITING & REJECTED

    Page<Booking> findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime start, LocalDateTime end, PageRequest pageRequest); //CURRENT

    Booking findFirstByItem_IdAndStartIsBeforeAndStatusOrderByStartDesc(Long itemId, LocalDateTime time, BookingStatus status);

    Booking findFirstByItem_IdAndStartIsAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime time, BookingStatus status);

    List<Booking> findAllByBooker_IdAndItem_IdAndEndIsBefore(Long bookerId, Long itemId, LocalDateTime time);
}
