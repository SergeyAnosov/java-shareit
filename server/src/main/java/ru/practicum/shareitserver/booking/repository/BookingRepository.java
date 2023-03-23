package ru.practicum.shareitserver.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareitserver.booking.BookingStatus;
import ru.practicum.shareitserver.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_IdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime time, Pageable pageable); //PAST

    List<Booking> findAllByBooker_IdAndEndIsAfterOrderByStartDesc(Long userId, LocalDateTime time, Pageable pageable); //FUTURE

    List<Booking> findAllByBooker_IdAndStatusEqualsOrderByStartDesc(Long userId, BookingStatus status, Pageable pageable); //WAITING & REJECTED

    List<Booking> findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable); //CURRENT


    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime time, Pageable pageable); //PAST

    List<Booking> findAllByItem_Owner_IdAndEndIsAfterOrderByStartDesc(Long userId, LocalDateTime time, Pageable pageable); //FUTURE

    List<Booking> findAllByItem_Owner_IdAndStatusEqualsOrderByStartDesc(Long userId, BookingStatus status, Pageable pageable); //WAITING & REJECTED

    List<Booking> findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable); //CURRENT

    Booking findFirstByItem_IdAndStartIsBeforeAndStatusOrderByStartDesc(Long itemId, LocalDateTime time, BookingStatus status);

    Booking findFirstByItem_IdAndStartIsAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime time, BookingStatus status);

    List<Booking> findAllByBooker_IdAndItem_IdAndEndIsBefore(Long bookerId, Long itemId, LocalDateTime time);
}
